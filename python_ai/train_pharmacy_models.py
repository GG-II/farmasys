# train_pharmacy_models.py - Script to train SARIMA models for pharmacy demand forecasting
import pandas as pd
import numpy as np
import pickle
import warnings
from datetime import datetime
from statsmodels.tsa.statespace.sarimax import SARIMAX
from sklearn.metrics import mean_absolute_error, mean_squared_error
import itertools
import os

warnings.filterwarnings('ignore')

def load_and_preprocess_data():
    """Load and preprocess pharmacy sales data"""
    
    # Load data
    data = pd.read_csv('pharmacy_otc_sales_data.csv', quotechar='"')
    
    print(f"Loaded dataset with {len(data)} records")
    print(f"Date range: {data['Date'].min()} - {data['Date'].max()}")
    print(f"Products: {data['Product'].nunique()}")
    print(f"Countries: {data['Country'].nunique()}")
    
    # Convert Date to datetime
    data['Date'] = pd.to_datetime(data['Date'])
    
    # Sort by date
    data = data.sort_values('Date')
    
    return data

def prepare_time_series(data, product_name=None, country=None):
    """Prepare time series data for a specific product and/or country"""
    
    filtered_data = data.copy()
    
    if product_name:
        filtered_data = filtered_data[filtered_data['Product'] == product_name]
    
    if country:
        filtered_data = filtered_data[filtered_data['Country'] == country]
    
    # Aggregate by date and sum boxes shipped
    ts_data = filtered_data.groupby('Date')['Boxes Shipped'].sum().sort_index()
    
    # Resample to weekly frequency to handle missing dates
    ts_data = ts_data.resample('W').sum()
    
    return ts_data

def find_best_sarima_order(ts_data, max_p=2, max_d=1, max_q=2, seasonal_period=4):
    """Find best SARIMA parameters using grid search"""
    
    print("\nSearching for optimal SARIMA parameters...")
    
    # Define parameter ranges
    p = range(0, max_p + 1)
    d = range(0, max_d + 1)
    q = range(0, max_q + 1)
    
    # Seasonal parameters
    P = range(0, 2)
    D = range(0, 2)
    Q = range(0, 2)
    s = seasonal_period
    
    # Generate all combinations
    pdq = list(itertools.product(p, d, q))
    seasonal_pdq = [(x[0], x[1], x[2], s) for x in itertools.product(P, D, Q)]
    
    best_aic = float('inf')
    best_order = None
    best_seasonal_order = None
    
    # Split data for validation
    train_size = int(len(ts_data) * 0.8)
    train, test = ts_data[:train_size], ts_data[train_size:]
    
    total_combinations = len(pdq) * len(seasonal_pdq)
    print(f"Testing {total_combinations} parameter combinations...")
    
    tested = 0
    for param in pdq:
        for param_seasonal in seasonal_pdq:
            try:
                model = SARIMAX(train,
                              order=param,
                              seasonal_order=param_seasonal,
                              enforce_stationarity=False,
                              enforce_invertibility=False)
                
                results = model.fit(disp=False)
                
                if results.aic < best_aic:
                    best_aic = results.aic
                    best_order = param
                    best_seasonal_order = param_seasonal
                
                tested += 1
                if tested % 10 == 0:
                    print(f"Progress: {tested}/{total_combinations} combinations tested...")
                    
            except Exception as e:
                continue
    
    print(f"\nBest SARIMA order: {best_order}")
    print(f"Best seasonal order: {best_seasonal_order}")
    print(f"Best AIC: {best_aic:.2f}")
    
    return best_order, best_seasonal_order

def train_sarima_models(data):
    """Train SARIMA models for each product"""
    
    print("\n" + "="*70)
    print("TRAINING SARIMA MODELS FOR PHARMACY DEMAND FORECASTING")
    print("="*70)
    
    products = data['Product'].unique()
    models = {}
    metadata = {}
    
    for product in products:
        print(f"\n{'='*70}")
        print(f"Training model for: {product}")
        print(f"{'='*70}")
        
        # Prepare time series for this product
        ts_product = prepare_time_series(data, product_name=product)
        
        print(f"Time series length: {len(ts_product)} weeks")
        print(f"Total boxes shipped: {ts_product.sum()}")
        print(f"Average weekly demand: {ts_product.mean():.2f}")
        
        # Find best parameters
        best_order, best_seasonal_order = find_best_sarima_order(ts_product)
        
        # Train final model on all data
        print("\nTraining final model on complete dataset...")
        model = SARIMAX(ts_product,
                       order=best_order,
                       seasonal_order=best_seasonal_order,
                       enforce_stationarity=False,
                       enforce_invertibility=False)
        
        fitted_model = model.fit(disp=False)
        
        # Calculate in-sample metrics
        predictions = fitted_model.fittedvalues
        mae = mean_absolute_error(ts_product, predictions)
        rmse = np.sqrt(mean_squared_error(ts_product, predictions))
        
        print(f"\nModel Performance:")
        print(f"  AIC: {fitted_model.aic:.2f}")
        print(f"  MAE: {mae:.2f} boxes")
        print(f"  RMSE: {rmse:.2f} boxes")
        
        # Save model
        models[product] = fitted_model
        
        # Save metadata
        metadata[product] = {
            'order': best_order,
            'seasonal_order': best_seasonal_order,
            'aic': fitted_model.aic,
            'mae': mae,
            'rmse': rmse,
            'last_value': float(ts_product.iloc[-1]),
            'mean_value': float(ts_product.mean()),
            'std_value': float(ts_product.std()),
            'total_observations': len(ts_product),
            'trend': 'Increasing' if ts_product.iloc[-1] > ts_product.iloc[0] else 'Decreasing'
        }
    
    return models, metadata

def save_models(models, metadata):
    """Save trained models and metadata"""
    
    # Create models directory if it doesn't exist
    if not os.path.exists('models'):
        os.makedirs('models')
        print("\nCreated 'models' directory")
    
    # Save each model
    for product, model in models.items():
        safe_filename = product.replace(' ', '_').lower()
        model.save(f'models/sarima_{safe_filename}_model.pkl')
        print(f"Saved model for {product}")
    
    # Save metadata
    metadata['training_date'] = datetime.now().isoformat()
    metadata['product_list'] = list(models.keys())
    
    with open('models/sarima_metadata.pkl', 'wb') as f:
        pickle.dump(metadata, f)
    
    print("\nAll models and metadata saved successfully!")

def main():
    """Main execution function"""
    
    print("="*70)
    print("PHARMACY DEMAND FORECASTING - MODEL TRAINING SCRIPT")
    print("="*70)
    print(f"Training started at: {datetime.now()}")
    print("="*70)
    
    # Load data
    print("\n1. Loading and preprocessing data...")
    data = load_and_preprocess_data()
    
    # Train models
    print("\n2. Training SARIMA models for each product...")
    try:
        models, metadata = train_sarima_models(data)
    except Exception as e:
        print(f"Error training models: {e}")
        return
    
    # Save models
    print("\n3. Saving models and metadata...")
    try:
        save_models(models, metadata)
    except Exception as e:
        print(f"Error saving models: {e}")
        return
    
    # Summary
    print("\n" + "="*70)
    print("TRAINING COMPLETED SUCCESSFULLY!")
    print("="*70)
    print(f"Total products: {len(models)}")
    print(f"Models saved in 'models/' directory")
    print(f"Training completed at: {datetime.now()}")
    print("\nTrained products:")
    for product in models.keys():
        print(f"  - {product}")
    
    print("\nNote: Models should be retrained periodically with new data")
    print("to maintain accuracy and adapt to changing demand patterns.")

if __name__ == "__main__":
    main()