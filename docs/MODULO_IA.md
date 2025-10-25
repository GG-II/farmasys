# 🤖 Módulo de Inteligencia Artificial - FarmaSys

Documentación completa del sistema de predicción de demanda con Machine Learning.

---

## 📋 Tabla de Contenidos

- [Visión General](#visión-general)
- [Modelo SARIMA](#modelo-sarima)
- [Entrenamiento](#entrenamiento)
- [Predicción](#predicción)
- [Integración](#integración)
- [Interpretación de Resultados](#interpretación-de-resultados)
- [Mantenimiento](#mantenimiento)

---

## 🎯 Visión General

El módulo de IA de FarmaSys utiliza **modelos SARIMA** (Seasonal AutoRegressive Integrated Moving Average) para predecir la demanda futura de medicamentos basándose en datos históricos de ventas.

### Características

- ✅ **62 modelos independientes** (uno por cada medicina)
- ✅ **Predicciones de 1 a 12 semanas**
- ✅ **Intervalos de confianza del 95%**
- ✅ **Recomendaciones de inventario automáticas**
- ✅ **Métricas de performance** (MAE, RMSE)
- ✅ **Detección de tendencias**

---

## 📊 Modelo SARIMA

### ¿Qué es SARIMA?

SARIMA es un modelo estadístico de series temporales que captura:

- **AR (AutoRegressive)**: Dependencia de valores pasados
- **I (Integrated)**: Diferenciación para estacionalidad
- **MA (Moving Average)**: Promedio de errores pasados
- **S (Seasonal)**: Patrones estacionales

### Fórmula del Modelo

```
SARIMA(p, d, q)(P, D, Q, s)
```

**Parámetros:**
- `p`: Orden autorregresivo
- `d`: Grado de diferenciación
- `q`: Orden de media móvil
- `P, D, Q`: Componentes estacionales
- `s`: Período estacional (4 semanas)

### Ejemplo para ABRILAR

```python
Model: SARIMA(0, 0, 2)(1, 1, 1, 4)

Interpretación:
- No tiene componente AR regular
- Sin diferenciación regular
- 2 términos de MA
- Componente estacional fuerte (P=1, D=1, Q=1)
- Período de 4 semanas
```

---

## 🏋️ Entrenamiento

### 1. Preparación de Datos

#### Requisitos de Datos
- **Mínimo**: 52 semanas (1 año) de historial
- **Recomendado**: 104 semanas (2 años)
- **Formato**: CSV con columnas específicas

#### Estructura del CSV

```csv
Date,Product,Country,Boxes Shipped
2024-01-01,ABRILAR,Guatemala,5
2024-01-08,ABRILAR,Guatemala,7
2024-01-15,ABRILAR,Guatemala,3
```

**Campos:**
- `Date`: Fecha de venta (YYYY-MM-DD)
- `Product`: Nombre de la medicina
- `Country`: País (informativo)
- `Boxes Shipped`: Cantidad vendida

---

### 2. Exportar Datos desde BD

```sql
SELECT 
    v.fecha AS Date,
    m.nombre AS Product,
    'Guatemala' AS Country,
    dv.cantidad AS 'Boxes Shipped'
FROM ventas v
INNER JOIN detalle_ventas dv ON v.id_venta = dv.id_venta
INNER JOIN medicina m ON dv.id_medicina = m.id
WHERE v.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
ORDER BY v.fecha, m.nombre;
```

Guardar como: `python_ai/pharmacy_otc_sales_data.csv`

---

### 3. Script de Entrenamiento

```python
# train_pharmacy_models.py

def train_sarima_model(data, product_name):
    """
    Entrena un modelo SARIMA para un producto
    """
    # 1. Preparar serie temporal
    ts_data = prepare_time_series(data, product_name)
    
    # 2. Dividir en train/test (80/20)
    train_size = int(len(ts_data) * 0.8)
    train, test = ts_data[:train_size], ts_data[train_size:]
    
    # 3. Buscar mejores parámetros
    best_params = grid_search_sarima(train)
    
    # 4. Entrenar modelo final
    model = SARIMAX(
        train,
        order=best_params['order'],
        seasonal_order=best_params['seasonal_order']
    )
    fitted_model = model.fit(disp=False)
    
    # 5. Validar con test set
    predictions = fitted_model.forecast(steps=len(test))
    mae = mean_absolute_error(test, predictions)
    rmse = np.sqrt(mean_squared_error(test, predictions))
    
    # 6. Guardar modelo
    fitted_model.save(f'models/sarima_{product_name}_model.pkl')
    
    return {
        'model': fitted_model,
        'mae': mae,
        'rmse': rmse,
        'params': best_params
    }
```

---

### 4. Ejecutar Entrenamiento

```bash
cd python_ai
python train_pharmacy_models.py
```

**Output esperado:**
```
Loaded dataset with 481 records
Date range: 2024-01-01 - 2025-10-24
Products: 62
Countries: 1

Training model for ABRILAR...
Best params: (0, 0, 2)(1, 1, 1, 4)
MAE: 0.30, RMSE: 0.43
✓ Modelo guardado

Training model for ACI-TIP...
Best params: (1, 0, 1)(1, 1, 1, 4)
MAE: 0.85, RMSE: 1.12
✓ Modelo guardado

...

✓ Successfully trained 62 models
Models saved to: ./models/
```

---

### 5. Archivos Generados

```
python_ai/models/
├── sarima_abrilar_model.pkl
├── sarima_aci-tip_model.pkl
├── sarima_acrea_model.pkl
├── ...
└── sarima_metadata.pkl  # Metadatos de todos los modelos
```

---

## 🔮 Predicción

### 1. Script de Predicción

```python
# pharmacy_demand_predictor.py

class PharmacyDemandPredictor:
    def predict_demand(self, product_name, weeks_ahead=4):
        """
        Predice demanda futura
        """
        # 1. Cargar modelo entrenado
        model = self.models[product_name]
        
        # 2. Hacer predicción
        forecast = model.forecast(steps=weeks_ahead)
        
        # 3. Calcular intervalos de confianza
        conf_int = model.get_forecast(steps=weeks_ahead).conf_int()
        
        # 4. Preparar resultados
        predictions = []
        for i in range(weeks_ahead):
            predictions.append({
                "week": i + 1,
                "date": calculate_date(i + 1),
                "predicted_demand": max(0, int(forecast.iloc[i])),
                "lower_bound": max(0, int(conf_int.iloc[i, 0])),
                "upper_bound": int(conf_int.iloc[i, 1])
            })
        
        # 5. Generar recomendaciones
        recommendations = generate_inventory_recommendations(
            predictions
        )
        
        return {
            "status": "success",
            "predictions": predictions,
            "recommendations": recommendations
        }
```

---

### 2. Uso desde Python

```python
from pharmacy_demand_predictor import PharmacyDemandPredictor

# Inicializar predictor
predictor = PharmacyDemandPredictor()

# Predecir demanda
result = predictor.predict_demand("ABRILAR", weeks_ahead=4)

print(f"Demanda total: {result['summary']['total_forecast']}")
print(f"Promedio semanal: {result['summary']['average_weekly_demand']}")
```

---

### 3. Uso desde Java

```java
// AIController.java
public class AIController {
    public static String predictDemand(Request req, Response res) {
        // Parsear request
        Map<String, Object> body = gson.fromJson(req.body(), Map.class);
        String productName = (String) body.get("productName");
        int weeksAhead = ((Double) body.get("weeksAhead")).intValue();
        
        // Ejecutar Python
        Map<String, Object> result = PythonExecutor.predictDemand(
            productName, 
            weeksAhead
        );
        
        // Retornar JSON
        return gson.toJson(result);
    }
}
```

---

### 4. Uso desde Frontend

```javascript
async function predecirDemanda() {
    const response = await fetch('http://localhost:4567/api/ai/predict', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            productName: 'ABRILAR',
            weeksAhead: 4
        })
    });
    
    const result = await response.json();
    mostrarGrafico(result.predictions);
}
```

---

## 📈 Interpretación de Resultados

### Estructura de Respuesta

```json
{
  "status": "success",
  "product": "ABRILAR",
  "predictions": [...],
  "summary": {
    "total_forecast": 24,
    "average_weekly_demand": 6.0,
    "peak_week": 3,
    "peak_demand": 8
  },
  "inventory_recommendations": {
    "safety_stock": 9,
    "reorder_point": 12,
    "recommended_order_quantity": 24
  },
  "model_info": {
    "mae": 0.30,
    "rmse": 0.43,
    "confidence": 0.85,
    "trend": "Stable"
  }
}
```

---

### Métricas del Modelo

#### MAE (Mean Absolute Error)
- **Qué es**: Error promedio absoluto
- **Interpretación**: 
  - MAE < 1: Excelente
  - MAE 1-2: Bueno
  - MAE > 2: Revisar modelo

#### RMSE (Root Mean Square Error)
- **Qué es**: Raíz del error cuadrático medio
- **Interpretación**: Penaliza errores grandes

#### Confianza
- **Rango**: 0.0 - 1.0
- **Interpretación**:
  - > 0.85: Alta confianza
  - 0.70-0.85: Confianza media
  - < 0.70: Baja confianza

---

### Recomendaciones de Inventario

#### Safety Stock (Stock de Seguridad)
```
safety_stock = avg_weekly_demand × 1.5
```
Cantidad mínima para evitar desabastecimiento.

#### Reorder Point (Punto de Reorden)
```
reorder_point = avg_weekly_demand × 2
```
Cuando el stock llega a este punto, hacer pedido.

#### Recommended Order Quantity
```
recommended_qty = total_forecast (para el período)
```
Cantidad sugerida para el próximo pedido.

---

### Tendencias

- **Increasing**: Demanda creciente
- **Decreasing**: Demanda decreciente  
- **Stable**: Demanda estable
- **Seasonal**: Variación estacional significativa

---

## 🔄 Integración

### Arquitectura de Integración

```
Frontend (JavaScript)
    ↓ HTTP POST
AIController (Java)
    ↓ ProcessBuilder
PythonExecutor (Java)
    ↓ Subprocess
pharmacy_demand_predictor.py (Python)
    ↓ Load Models
Modelos SARIMA (.pkl files)
    ↓ Forecast
Predicción JSON
    ↓ Return
Frontend (Chart.js)
```

---

### Flujo Completo

1. **Usuario** selecciona medicina en frontend
2. **JavaScript** hace POST a `/api/ai/predict`
3. **AIController** recibe request
4. **PythonExecutor** ejecuta script Python
5. **Python** carga modelo correspondiente
6. **SARIMA** genera predicción
7. **Python** retorna JSON
8. **Java** parsea y envía al frontend
9. **Frontend** renderiza gráfico

---

## 🛠️ Mantenimiento

### Re-entrenamiento

**Cuándo re-entrenar:**
- Cada 3 meses (recomendado)
- Cuando cambien patrones de demanda
- Después de eventos significativos
- Si la precisión disminuye

**Cómo re-entrenar:**

```bash
# 1. Exportar nuevos datos
mysql -u root -p clinica_farmacia < export_sales.sql > new_data.csv

# 2. Reemplazar CSV
mv new_data.csv python_ai/pharmacy_otc_sales_data.csv

# 3. Eliminar modelos viejos
rm -rf python_ai/models/

# 4. Re-entrenar
cd python_ai
python train_pharmacy_models.py

# 5. Verificar
python test_predictor.py
```

---

### Monitoreo de Performance

#### Script de Validación

```python
def validate_models():
    """
    Valida todos los modelos con datos recientes
    """
    results = []
    
    for product in products:
        # Obtener datos reales recientes
        actual = get_recent_sales(product, weeks=4)
        
        # Predecir
        predicted = predictor.predict_demand(product, weeks_ahead=4)
        
        # Calcular error
        error = calculate_error(actual, predicted)
        
        results.append({
            'product': product,
            'mae': error['mae'],
            'accuracy': 1 - (error['mae'] / actual.mean())
        })
    
    return results
```

---

### Optimización de Modelos

#### Grid Search para Mejores Parámetros

```python
def grid_search_sarima(data):
    """
    Busca los mejores parámetros SARIMA
    """
    p = d = q = range(0, 3)
    pdq = list(itertools.product(p, d, q))
    seasonal_pdq = [(x[0], x[1], x[2], 4) for x in pdq]
    
    best_aic = float('inf')
    best_params = None
    
    for param in pdq:
        for param_seasonal in seasonal_pdq:
            try:
                model = SARIMAX(
                    data,
                    order=param,
                    seasonal_order=param_seasonal
                )
                results = model.fit(disp=False)
                
                if results.aic < best_aic:
                    best_aic = results.aic
                    best_params = (param, param_seasonal)
            except:
                continue
    
    return best_params
```

---

## 📊 Casos de Uso

### 1. Planificación de Compras

```
Objetivo: Determinar qué medicinas comprar
Uso: predict_all_products()
Acción: Ordenar por demanda predicha
```

### 2. Gestión de Stock

```
Objetivo: Evitar desabastecimiento
Uso: safety_stock + reorder_point
Acción: Alertas automáticas
```

### 3. Análisis de Tendencias

```
Objetivo: Identificar cambios en demanda
Uso: Comparar predicciones vs ventas reales
Acción: Ajustar estrategia
```

---

## 🚨 Limitaciones

1. **Datos Históricos**: Requiere al menos 1 año
2. **Eventos Atípicos**: No predice eventos excepcionales
3. **Nuevos Productos**: Sin historial no se pueden predecir
4. **Estacionalidad**: Asume patrones repetibles
5. **Factores Externos**: No considera promociones, competencia, etc.

---

## 📚 Referencias

- [Statsmodels SARIMAX](https://www.statsmodels.org/stable/generated/statsmodels.tsa.statespace.sarimax.SARIMAX.html)
- [Time Series Forecasting](https://otexts.com/fpp2/)
- [ARIMA Models](https://en.wikipedia.org/wiki/Autoregressive_integrated_moving_average)

---

## 🔗 Enlaces Relacionados

- [API Documentation](API.md)
- [Arquitectura](ARQUITECTURA.md)
- [Instalación](INSTALACION.md)

---

**Módulo IA Version:** 1.0  
**Última actualización:** Octubre 2025