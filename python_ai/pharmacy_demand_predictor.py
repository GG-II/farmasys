import json
import sys
import logging
import pandas as pd
import numpy as np
import pickle
from datetime import datetime, timedelta
from statsmodels.tsa.statespace.sarimax import SARIMAXResults
import warnings
import os

warnings.filterwarnings('ignore')

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class PharmacyDemandPredictor:
    def __init__(self):
        self.models_loaded = False
        self.models = {}
        self.metadata = None
        
        # Load models
        self.load_models()
    
    def load_models(self):
        """Load trained SARIMA models"""
        try:
            models_dir = 'python_ai/models'
            
            if not os.path.exists(models_dir):
                raise FileNotFoundError("Models directory not found. Run train_pharmacy_models.py first.")
            
            # Load metadata
            metadata_path = os.path.join(models_dir, 'sarima_metadata.pkl')
            if os.path.exists(metadata_path):
                with open(metadata_path, 'rb') as f:
                    self.metadata = pickle.load(f)
                logger.info("Metadata loaded successfully")
            else:
                logger.warning("Metadata file not found")
                return
            
            # Load each product model
            product_list = self.metadata.get('product_list', [])
            
            for product in product_list:
                safe_filename = product.replace(' ', '_').lower()
                model_path = os.path.join(models_dir, f'sarima_{safe_filename}_model.pkl')
                
                if os.path.exists(model_path):
                    self.models[product] = SARIMAXResults.load(model_path)
                    logger.info(f"Loaded model for {product}")
                else:
                    logger.warning(f"Model file not found for {product}")
            
            if len(self.models) > 0:
                self.models_loaded = True
                logger.info(f"Successfully loaded {len(self.models)} product models")
            else:
                logger.error("No models were loaded")
                
        except Exception as e:
            logger.error(f"Error loading models: {e}")
            self.models_loaded = False
    
    def predict_demand(self, product_name, weeks_ahead=4):
        """Predict future demand for a specific product"""
        
        try:
            if not self.models_loaded:
                return {
                    "status": "error",
                    "message": "Models not loaded. Run train_pharmacy_models.py first.",
                    "confidence": 0.0
                }
            
            if product_name not in self.models:
                available_products = list(self.models.keys())
                return {
                    "status": "error",
                    "message": f"Product '{product_name}' not found. Available products: {available_products}",
                    "confidence": 0.0
                }
            
            # Get model and metadata for this product
            model = self.models[product_name]
            product_metadata = self.metadata.get(product_name, {})
            
            # Make predictions
            forecast = model.forecast(steps=weeks_ahead)
            
            # Get prediction intervals
            try:
                forecast_obj = model.get_forecast(steps=weeks_ahead)
                conf_int = forecast_obj.conf_int()
            except Exception as e:
                logger.warning(f"Could not get confidence intervals: {e}")
                # Create approximate confidence intervals
                std = product_metadata.get('std_value', forecast.std())
                conf_int = pd.DataFrame({
                    'lower Boxes Shipped': forecast - 1.96 * std,
                    'upper Boxes Shipped': forecast + 1.96 * std
                })
            
            # Prepare predictions
            predictions = []
            current_date = datetime.now()
            
            for i in range(weeks_ahead):
                week_date = current_date + timedelta(weeks=i+1)
                
                predictions.append({
                    "week": i + 1,
                    "date": week_date.strftime('%Y-%m-%d'),
                    "predicted_demand": max(0, int(round(forecast.iloc[i]))),
                    "lower_bound": max(0, int(round(conf_int.iloc[i, 0]))),
                    "upper_bound": int(round(conf_int.iloc[i, 1]))
                })
            
            # Calculate total forecast
            total_forecast = sum(p['predicted_demand'] for p in predictions)
            
            # Calculate confidence based on model performance
            mae = product_metadata.get('mae', 0)
            mean_value = product_metadata.get('mean_value', 1)
            
            if mean_value > 0:
                mape = (mae / mean_value) * 100
                confidence = max(0.5, min(0.95, 1 - (mape / 100)))
            else:
                confidence = 0.75
            
            # Inventory recommendations
            avg_weekly_demand = total_forecast / weeks_ahead
            safety_stock = avg_weekly_demand * 1.5  # 1.5 weeks of safety stock
            reorder_point = avg_weekly_demand * 2  # Reorder when 2 weeks remain
            
            return {
                "status": "success",
                "product": product_name,
                "model_type": "SARIMA",
                "forecast_period": f"{weeks_ahead} weeks",
                "predictions": predictions,
                "summary": {
                    "total_forecast": total_forecast,
                    "average_weekly_demand": round(avg_weekly_demand, 2),
                    "peak_week": max(predictions, key=lambda x: x['predicted_demand'])['week'],
                    "peak_demand": max(p['predicted_demand'] for p in predictions)
                },
                "inventory_recommendations": {
                    "safety_stock": int(round(safety_stock)),
                    "reorder_point": int(round(reorder_point)),
                    "recommended_order_quantity": total_forecast,
                    "lead_time_coverage": "4 weeks"
                },
                "model_info": {
                    "order": product_metadata.get('order'),
                    "seasonal_order": product_metadata.get('seasonal_order'),
                    "mae": round(product_metadata.get('mae', 0), 2),
                    "rmse": round(product_metadata.get('rmse', 0), 2),
                    "last_observed_demand": product_metadata.get('last_value'),
                    "historical_average": round(product_metadata.get('mean_value', 0), 2),
                    "trend": product_metadata.get('trend')
                },
                "confidence": round(confidence, 4)
            }
            
        except Exception as e:
            logger.error(f"Error making prediction: {e}")
            return {
                "status": "error",
                "message": str(e),
                "confidence": 0.0
            }
    
    def get_all_products_forecast(self, weeks_ahead=4):
        """Get demand forecast for all products"""
        
        try:
            if not self.models_loaded:
                return {
                    "status": "error",
                    "message": "Models not loaded. Run train_pharmacy_models.py first."
                }
            
            all_forecasts = {}
            
            for product in self.models.keys():
                forecast_result = self.predict_demand(product, weeks_ahead)
                
                if forecast_result['status'] == 'success':
                    all_forecasts[product] = {
                        "total_forecast": forecast_result['summary']['total_forecast'],
                        "average_weekly": forecast_result['summary']['average_weekly_demand'],
                        "reorder_point": forecast_result['inventory_recommendations']['reorder_point'],
                        "confidence": forecast_result['confidence']
                    }
            
            # Sort by total forecast demand
            sorted_products = sorted(all_forecasts.items(), 
                                   key=lambda x: x[1]['total_forecast'], 
                                   reverse=True)
            
            return {
                "status": "success",
                "forecast_period": f"{weeks_ahead} weeks",
                "total_products": len(all_forecasts),
                "products": dict(sorted_products),
                "summary": {
                    "total_demand_all_products": sum(p['total_forecast'] for p in all_forecasts.values()),
                    "top_product": sorted_products[0][0] if sorted_products else None,
                    "highest_demand": sorted_products[0][1]['total_forecast'] if sorted_products else 0
                }
            }
            
        except Exception as e:
            logger.error(f"Error in all products forecast: {e}")
            return {
                "status": "error",
                "message": str(e)
            }

def process_parameters(parameters):
    """Process prediction request parameters"""
    
    try:
        predictor = PharmacyDemandPredictor()
        
        if not predictor.models_loaded:
            return {
                "status": "error",
                "message": "Models not loaded. Run train_pharmacy_models.py to train models first.",
                "timestamp": datetime.now().isoformat()
            }
        
        # Get parameters
        prediction_type = parameters.get('prediction_type', 'single_product')
        product_name = parameters.get('product_name', None)
        weeks_ahead = int(parameters.get('weeks_ahead', 4))
        
        # Validate weeks_ahead
        if weeks_ahead < 1 or weeks_ahead > 12:
            return {
                "status": "error",
                "message": "weeks_ahead must be between 1 and 12",
                "timestamp": datetime.now().isoformat()
            }
        
        if prediction_type == 'single_product':
            if not product_name:
                return {
                    "status": "error",
                    "message": "product_name is required for single product prediction",
                    "available_products": list(predictor.models.keys()),
                    "timestamp": datetime.now().isoformat()
                }
            
            result = predictor.predict_demand(product_name, weeks_ahead)
            
        elif prediction_type == 'all_products':
            result = predictor.get_all_products_forecast(weeks_ahead)
            
        else:
            return {
                "status": "error",
                "message": f"Unknown prediction_type: {prediction_type}. Use 'single_product' or 'all_products'",
                "timestamp": datetime.now().isoformat()
            }
        
        # Add timestamp
        result['timestamp'] = datetime.now().isoformat()
        result['processing_time'] = "< 1 second"
        
        return result
        
    except ValueError as e:
        logger.error(f"Value error: {e}")
        return {
            "status": "error",
            "message": f"Invalid parameters: {str(e)}",
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        logger.error(f"Unexpected error: {e}")
        return {
            "status": "error",
            "message": f"Unexpected error: {str(e)}",
            "timestamp": datetime.now().isoformat()
        }

def main():
    """Main execution function"""
    
    try:
        if len(sys.argv) != 2:
            raise ValueError("Parameters must be provided as a single JSON argument.")
        
        parameters_json = sys.argv[1]
        parameters = json.loads(parameters_json)
        
        logger.info(f"Received parameters: {parameters}")
        
        result = process_parameters(parameters)
        
        print(json.dumps(result, indent=2, ensure_ascii=False))
        
    except json.JSONDecodeError as e:
        error_result = {
            "status": "error",
            "message": f"Invalid JSON parameters: {str(e)}",
            "timestamp": datetime.now().isoformat()
        }
        print(json.dumps(error_result, indent=2, ensure_ascii=False))
        sys.exit(1)
        
    except Exception as e:
        error_result = {
            "status": "error",
            "message": f"Script execution failed: {str(e)}",
            "timestamp": datetime.now().isoformat()
        }
        print(json.dumps(error_result, indent=2, ensure_ascii=False))
        sys.exit(1)

if __name__ == "__main__":
    main()