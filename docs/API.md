# 🌐 Documentación API REST - FarmaSys

API RESTful completa para la gestión de farmacias.

---

## 📋 Tabla de Contenidos

- [Información General](#información-general)
- [API de Medicinas](#api-de-medicinas)
- [API de Clientes](#api-de-clientes)
- [API de Ventas](#api-de-ventas)
- [API de Inteligencia Artificial](#api-de-inteligencia-artificial)
- [Códigos de Estado](#códigos-de-estado)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## ℹ️ Información General

### Base URL
```
http://localhost:4567/api
```

### Formato de Respuesta
Todas las respuestas son en formato JSON.

### Headers
```
Content-Type: application/json
Accept: application/json
```

---

## 💊 API de Medicinas

### 1. Obtener Todas las Medicinas

```http
GET /api/medicinas
```

**Respuesta exitosa (200):**
```json
[
  {
    "id": 1,
    "nombre": "ABRILAR",
    "dosis": "200 ml",
    "precio": 140.0,
    "cantidad": 50,
    "fechaCaducidad": "2025-12-31",
    "descripcion": "Jarabe para la tos",
    "tipoID": 4,
    "tipo": "JARABE"
  }
]
```

---

### 2. Obtener Medicina por ID

```http
GET /api/medicinas/:id
```

**Parámetros:**
- `id` (path) - ID de la medicina

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "nombre": "ABRILAR",
  "dosis": "200 ml",
  "precio": 140.0,
  "cantidad": 50,
  "fechaCaducidad": "2025-12-31",
  "descripcion": "Jarabe para la tos",
  "tipoID": 4,
  "tipo": "JARABE"
}
```

**Error (404):**
```json
{
  "error": "Medicina no encontrada"
}
```

---

### 3. Buscar Medicinas por Nombre

```http
GET /api/medicinas/buscar/:termino
```

**Parámetros:**
- `termino` (path) - Término de búsqueda

**Ejemplo:**
```http
GET /api/medicinas/buscar/ABRILAR
```

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "nombre": "ABRILAR",
    ...
  }
]
```

---

### 4. Crear Nueva Medicina

```http
POST /api/medicinas
```

**Body:**
```json
{
  "nombre": "NUEVA MEDICINA",
  "dosis": "500mg",
  "precio": 25.50,
  "cantidad": 100,
  "fechaCaducidad": "2026-01-01",
  "descripcion": "Descripción",
  "tipoID": 3
}
```

**Respuesta exitosa (201):**
```json
{
  "message": "Medicina agregada exitosamente"
}
```

**Error de validación (400):**
```json
{
  "error": "El nombre es requerido"
}
```

---

### 5. Actualizar Medicina

```http
PUT /api/medicinas/:id
```

**Body:**
```json
{
  "id": 1,
  "nombre": "ABRILAR ACTUALIZADO",
  "dosis": "200 ml",
  "precio": 150.0,
  "cantidad": 60,
  "fechaCaducidad": "2025-12-31",
  "descripcion": "Descripción actualizada",
  "tipoID": 4
}
```

**Respuesta (200):**
```json
{
  "message": "Medicina actualizada exitosamente"
}
```

---

### 6. Obtener Tipos de Medicina

```http
GET /api/medicinas/tipos/lista
```

**Respuesta (200):**
```json
[
  "AMPOLLAS",
  "BLISTER",
  "TABLETAS",
  "JARABE",
  "GEL",
  "CREMA",
  "SOBRES",
  "SPRAY"
]
```

---

### 7. Filtros Especiales

#### Medicinas Disponibles (stock > 0)
```http
GET /api/medicinas/disponibles
```

#### Medicinas con Pocas Existencias (stock < 10)
```http
GET /api/medicinas/pocas-existencias
```

#### Medicinas Agotadas (stock = 0)
```http
GET /api/medicinas/agotadas
```

#### Medicinas por Vencer (< 60 días)
```http
GET /api/medicinas/por-vencer
```

---

## 👥 API de Clientes

### 1. Obtener Todos los Clientes

```http
GET /api/clientes
```

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "nombre1": "Juan",
    "nombre2": "Carlos",
    "apellido1": "Pérez",
    "apellido2": "López",
    "nit": "12345678",
    "telefono": "12345678",
    "direccion": "Zona 1, Guatemala"
  }
]
```

---

### 2. Obtener Cliente por ID

```http
GET /api/clientes/:id
```

**Respuesta (200):**
```json
{
  "id": 1,
  "nombre1": "Juan",
  "nombre2": "Carlos",
  "apellido1": "Pérez",
  "apellido2": "López",
  "nit": "12345678",
  "telefono": "12345678",
  "direccion": "Zona 1, Guatemala"
}
```

---

### 3. Buscar Clientes

```http
GET /api/clientes/buscar/:termino
```

**Ejemplo:**
```http
GET /api/clientes/buscar/Juan
```

---

### 4. Crear Cliente

```http
POST /api/clientes
```

**Body:**
```json
{
  "nombre1": "Juan",
  "nombre2": "Carlos",
  "apellido1": "Pérez",
  "apellido2": "López",
  "nit": "12345678",
  "telefono": "12345678",
  "direccion": "Zona 1, Guatemala"
}
```

**Respuesta (201):**
```json
{
  "message": "Cliente agregado exitosamente"
}
```

---

### 5. Actualizar Cliente

```http
PUT /api/clientes/:id
```

**Body:** Igual que POST con campo `id`

---

## 🛒 API de Ventas

### 1. Obtener Ventas por Período

```http
GET /api/ventas?periodo=:periodo
```

**Parámetros:**
- `periodo` (query): "Hoy", "Esta semana", "Últimos 30 días", "Este año", "Total"

**Ejemplo:**
```http
GET /api/ventas?periodo=Hoy
```

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "fecha": "2025-10-24",
    "total": 450.50,
    "nombreCliente": "Juan Carlos Pérez López"
  }
]
```

---

### 2. Obtener Venta por ID

```http
GET /api/ventas/:id
```

**Respuesta (200):**
```json
{
  "id": 1,
  "fecha": "2025-10-24",
  "total": 450.50,
  "idCliente": 1,
  "nombreCliente": "Juan Carlos Pérez López",
  "detalles": [
    {
      "id": 1,
      "idVenta": 1,
      "idMedicina": 1,
      "cantidad": 3,
      "precio": 150.0,
      "medicina": {
        "nombre": "ABRILAR"
      }
    }
  ]
}
```

---

### 3. Crear Venta

```http
POST /api/ventas
```

**Body:**
```json
{
  "fecha": "2025-10-24",
  "total": 450.50,
  "idCliente": 1,
  "detalles": [
    {
      "idMedicina": 1,
      "cantidad": 3,
      "precio": 150.0
    },
    {
      "idMedicina": 2,
      "cantidad": 1,
      "precio": 50.0
    }
  ]
}
```

**Respuesta (201):**
```json
{
  "message": "Venta registrada exitosamente",
  "idVenta": 15
}
```

---

### 4. Actualizar Venta

```http
PUT /api/ventas/:id
```

**Body:** Similar a POST con campo `id`

---

### 5. Eliminar Venta

```http
DELETE /api/ventas/:id
```

**Respuesta (200):**
```json
{
  "message": "Venta eliminada exitosamente"
}
```

---

## 🤖 API de Inteligencia Artificial

### 1. Predecir Demanda de Producto

```http
POST /api/ai/predict
```

**Body:**
```json
{
  "productName": "ABRILAR",
  "weeksAhead": 4
}
```

**Respuesta (200):**
```json
{
  "status": "success",
  "product": "ABRILAR",
  "model_type": "SARIMA",
  "forecast_period": "4 weeks",
  "predictions": [
    {
      "week": 1,
      "date": "2025-10-31",
      "predicted_demand": 5,
      "lower_bound": 2,
      "upper_bound": 8
    },
    {
      "week": 2,
      "date": "2025-11-07",
      "predicted_demand": 7,
      "lower_bound": 4,
      "upper_bound": 10
    }
  ],
  "summary": {
    "total_forecast": 24,
    "average_weekly_demand": 6.0,
    "peak_week": 3,
    "peak_demand": 8
  },
  "inventory_recommendations": {
    "safety_stock": 9,
    "reorder_point": 12,
    "recommended_order_quantity": 24,
    "lead_time_coverage": "4 weeks"
  },
  "model_info": {
    "order": [0, 0, 2],
    "seasonal_order": [1, 1, 1, 4],
    "mae": 1.2,
    "rmse": 1.5,
    "last_observed_demand": 5,
    "historical_average": 6.3,
    "trend": "Stable"
  },
  "confidence": 0.85,
  "timestamp": "2025-10-24T20:15:00"
}
```

---

### 2. Predecir Demanda de Todos los Productos

```http
GET /api/ai/predict-all?weeks=4
```

**Parámetros:**
- `weeks` (query) - Número de semanas (1-12)

**Respuesta (200):**
```json
{
  "status": "success",
  "forecast_period": "4 weeks",
  "total_products": 62,
  "products": {
    "ABRILAR": {
      "total_forecast": 24,
      "average_weekly": 6.0,
      "reorder_point": 12,
      "confidence": 0.85
    },
    "ACI-TIP": {
      "total_forecast": 30,
      "average_weekly": 7.5,
      "reorder_point": 15,
      "confidence": 0.82
    }
  },
  "summary": {
    "total_demand_all_products": 1250,
    "top_product": "METFORMINA DENK",
    "highest_demand": 45
  }
}
```

---

## 📊 Códigos de Estado HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Datos inválidos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## 💡 Ejemplos de Uso

### JavaScript (Fetch API)

#### Obtener medicinas:
```javascript
fetch('http://localhost:4567/api/medicinas')
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
```

#### Crear medicina:
```javascript
const medicina = {
    nombre: "NUEVA MEDICINA",
    dosis: "500mg",
    precio: 25.50,
    cantidad: 100,
    fechaCaducidad: "2026-01-01",
    descripcion: "Descripción",
    tipoID: 3
};

fetch('http://localhost:4567/api/medicinas', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(medicina)
})
.then(response => response.json())
.then(data => console.log(data));
```

---

### cURL

#### Obtener medicinas:
```bash
curl http://localhost:4567/api/medicinas
```

#### Crear cliente:
```bash
curl -X POST http://localhost:4567/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre1": "Juan",
    "apellido1": "Pérez",
    "nit": "12345678",
    "telefono": "12345678",
    "direccion": "Zona 1"
  }'
```

#### Predecir demanda:
```bash
curl -X POST http://localhost:4567/api/ai/predict \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "ABRILAR",
    "weeksAhead": 4
  }'
```

---

### Python (Requests)

```python
import requests

# Obtener medicinas
response = requests.get('http://localhost:4567/api/medicinas')
medicinas = response.json()

# Crear venta
venta = {
    "fecha": "2025-10-24",
    "total": 450.50,
    "idCliente": 1,
    "detalles": [
        {"idMedicina": 1, "cantidad": 3, "precio": 150.0}
    ]
}

response = requests.post(
    'http://localhost:4567/api/ventas',
    json=venta
)
print(response.json())
```

---

## 🔒 Notas de Seguridad

1. **Validación**: Todos los endpoints validan datos de entrada
2. **SQL Injection**: Protección mediante PreparedStatements
3. **XSS**: Sanitización de datos
4. **Rate Limiting**: Implementar en producción
5. **HTTPS**: Usar en producción

---

## 📝 Notas Adicionales

- Todas las fechas en formato ISO 8601: `YYYY-MM-DD`
- Precios en formato decimal con 2 decimales
- IDs son enteros autoincrementales
- Campos requeridos marcados en documentación

---

## 🔗 Enlaces Relacionados

- [Arquitectura](ARQUITECTURA.md)
- [Base de Datos](BASE_DE_DATOS.md)
- [Módulo IA](MODULO_IA.md)

---

**API Version:** 1.0  
**Última actualización:** Octubre 2025