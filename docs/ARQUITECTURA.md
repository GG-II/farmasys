# 🏗️ Arquitectura del Sistema - FarmaSys

Esta documentación describe la arquitectura técnica y el diseño del sistema FarmaSys.

---

## 📋 Tabla de Contenidos

- [Visión General](#visión-general)
- [Arquitectura en Capas](#arquitectura-en-capas)
- [Componentes Principales](#componentes-principales)
- [Flujo de Datos](#flujo-de-datos)
- [Patrones de Diseño](#patrones-de-diseño)
- [Diagrama de Componentes](#diagrama-de-componentes)

---

## 🎯 Visión General

FarmaSys utiliza una arquitectura de **3 capas** con separación clara de responsabilidades:

```
┌─────────────────────────────────────┐
│         CAPA PRESENTACIÓN           │
│    (HTML/CSS/JS - Bootstrap 5)      │
└─────────────────────────────────────┘
                 ↕ HTTP/JSON
┌─────────────────────────────────────┐
│         CAPA APLICACIÓN             │
│  (Java 11 - Spark Framework)        │
│  Controllers → Services → DAO        │
└─────────────────────────────────────┘
                 ↕ JDBC
┌─────────────────────────────────────┐
│         CAPA DATOS                  │
│      (MariaDB 10.x)                 │
└─────────────────────────────────────┘
```

---

## 🏛️ Arquitectura en Capas

### 1. Capa de Presentación (Frontend)

**Tecnologías:**
- HTML5, CSS3, JavaScript ES6
- Bootstrap 5.3 para UI/UX
- Chart.js para visualizaciones
- Font Awesome para iconos

**Responsabilidades:**
- Renderizado de interfaces
- Validación de formularios
- Comunicación con API REST
- Experiencia de usuario

**Estructura:**
```
public/
├── index.html          # Dashboard
├── medicinas.html      # Gestión medicinas
├── clientes.html       # Gestión clientes
├── ventas.html         # Sistema de ventas
├── ia.html            # Predicción IA
├── css/
│   └── styles.css
└── js/
    ├── medicinas.js
    ├── clientes.js
    ├── ventas.js
    └── ia.js
```

---

### 2. Capa de Aplicación (Backend)

**Tecnologías:**
- Java 11
- Spark Framework 2.9.4
- Gson 2.10.1
- Maven

**Responsabilidades:**
- Lógica de negocio
- Procesamiento de requests
- Validación de datos
- Gestión de sesiones
- Integración con módulo IA

**Estructura:**
```
src/main/java/
├── Main.java                    # Punto de entrada
├── controllers/                 # Controladores REST
│   ├── MedicinaController.java
│   ├── ClienteController.java
│   ├── VentaController.java
│   └── AIController.java
├── dao/                         # Acceso a datos
│   ├── MedicinaDAO.java
│   ├── ClienteDAO.java
│   └── VentaDAO.java
├── model/                       # Modelos de dominio
│   ├── Medicina.java
│   ├── Cliente.java
│   ├── Venta.java
│   └── DetalleVenta.java
├── services/                    # Servicios
│   └── PythonExecutor.java
└── config/                      # Configuración
    └── conexionBD.java
```

---

### 3. Capa de Datos

**Tecnologías:**
- MariaDB 10.6
- JDBC Driver

**Responsabilidades:**
- Persistencia de datos
- Integridad referencial
- Transacciones ACID
- Consultas optimizadas

**Esquema:**
```
clinica_farmacia
├── medicina          # Catálogo de medicamentos
├── tipo_medicina     # Tipos (Tabletas, Jarabe, etc.)
├── clientes          # Registro de clientes
├── ventas            # Cabecera de ventas
└── detalle_ventas    # Líneas de venta
```

---

## 🧩 Componentes Principales

### 1. Controladores (Controllers)

**Función:** Reciben requests HTTP y delegan a servicios/DAOs.

**Ejemplo - MedicinaController:**
```java
public class MedicinaController {
    
    public static void registrarRutas() {
        // GET - Obtener todas
        get("/api/medicinas", (req, res) -> obtenerTodas(req, res));
        
        // GET - Obtener por ID
        get("/api/medicinas/:id", (req, res) -> obtenerPorId(req, res));
        
        // POST - Crear nueva
        post("/api/medicinas", (req, res) -> crear(req, res));
        
        // PUT - Actualizar
        put("/api/medicinas/:id", (req, res) -> actualizar(req, res));
        
        // DELETE - Eliminar
        delete("/api/medicinas/:id", (req, res) -> eliminar(req, res));
    }
}
```

**Características:**
- Manejo de errores centralizado
- Validación de entrada
- Respuestas JSON estandarizadas
- Logging de operaciones

---

### 2. DAO (Data Access Object)

**Función:** Abstrae el acceso a la base de datos.

**Ejemplo - MedicinaDAO:**
```java
public class MedicinaDAO {
    
    public List<Medicina> obtenerTodasLasMedicinas() throws SQLException {
        // Consulta SQL
        // Mapeo de ResultSet a objetos
        // Manejo de conexiones
    }
    
    public void agregarMedicina(Medicina medicina) throws SQLException {
        // Insert SQL
        // Prepared Statements
        // Prevención de SQL Injection
    }
}
```

**Patrones implementados:**
- Connection pooling
- PreparedStatements (seguridad)
- Try-with-resources (manejo de recursos)
- Manejo de transacciones

---

### 3. Modelos (Models)

**Función:** Representan entidades del dominio.

**Ejemplo - Medicina:**
```java
public class Medicina {
    private int id;
    private String nombre;
    private String dosis;
    private double precio;
    private int cantidad;
    private Date fechaCaducidad;
    private String descripcion;
    private int tipoID;
    private String tipo;
    
    // Getters, setters, constructores
}
```

**Características:**
- POJOs simples
- Sin lógica de negocio
- Serialización JSON automática (Gson)

---

### 4. Servicios (Services)

**Función:** Lógica de negocio compleja y orquestación.

**Ejemplo - PythonExecutor:**
```java
public class PythonExecutor {
    
    public static Map<String, Object> predictDemand(
        String productName, 
        int weeksAhead
    ) {
        // Crear JSON para Python
        // Ejecutar script Python
        // Capturar resultado
        // Parsear y retornar
    }
}
```

---

## 🔄 Flujo de Datos

### Flujo de Consulta (GET)

```
Usuario → Frontend → Controller → DAO → BD
                ↓                    ↓
              JSON ← Gson ← Model ← ResultSet
```

**Ejemplo: Obtener medicinas**
```
1. Usuario abre medicinas.html
2. JavaScript hace: fetch('/api/medicinas')
3. MedicinaController.obtenerTodas()
4. MedicinaDAO.obtenerTodasLasMedicinas()
5. Ejecuta: SELECT * FROM medicina
6. Mapea ResultSet → List<Medicina>
7. Gson convierte a JSON
8. Response enviada al frontend
9. JavaScript renderiza tabla
```

---

### Flujo de Creación (POST)

```
Usuario → Form → Validación → JSON → Controller
                                        ↓
                                      DAO
                                        ↓
                                     INSERT
                                        ↓
                                       BD
```

**Ejemplo: Agregar medicina**
```
1. Usuario llena formulario
2. JavaScript valida campos
3. Crea objeto JSON
4. POST /api/medicinas con body
5. MedicinaController.crear()
6. Parsea JSON → Medicina object
7. Valida datos
8. MedicinaDAO.agregarMedicina()
9. INSERT INTO medicina...
10. Retorna success/error
11. Frontend muestra mensaje
```

---

### Flujo de Predicción IA

```
Usuario → Frontend → AIController → PythonExecutor
                                         ↓
                                    Python Script
                                         ↓
                                    Modelo SARIMA
                                         ↓
                                    Predicción JSON
                                         ↓
                                    Frontend (Chart)
```

---

## 🎨 Patrones de Diseño

### 1. MVC (Model-View-Controller)

```
Model:      Medicina, Cliente, Venta
View:       HTML/JS (Frontend)
Controller: MedicinaController, etc.
```

### 2. DAO (Data Access Object)

Separa lógica de acceso a datos del negocio.

```java
Interface → DAO Implementation → Database
```

### 3. Singleton

Para conexión a BD:

```java
public class conexionBD {
    private static Connection conexion = null;
    
    public static Connection getConexion() {
        if (conexion == null) {
            // Crear conexión
        }
        return conexion;
    }
}
```

### 4. Factory Pattern

Para crear objetos complejos:

```java
public class ResponseFactory {
    public static String success(Object data) {
        return gson.toJson(Map.of("status", "success", "data", data));
    }
    
    public static String error(String message) {
        return gson.toJson(Map.of("status", "error", "message", message));
    }
}
```

---

## 📊 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────┐
│                    FRONTEND                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │Dashboard │ │Medicinas │ │  Ventas  │           │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘           │
│       │            │             │                  │
│       └────────────┴─────────────┘                  │
│                    │ HTTP/JSON                      │
└────────────────────┼───────────────────────────────┘
                     │
┌────────────────────┼───────────────────────────────┐
│                BACKEND                              │
│       ┌────────────▼──────────────┐                │
│       │   Spark Web Server        │                │
│       │   (Port 4567)             │                │
│       └────────────┬──────────────┘                │
│                    │                                │
│       ┌────────────┴──────────────┐                │
│       │     Controllers           │                │
│       │  ┌─────────────────────┐  │                │
│       │  │ MedicinaController  │  │                │
│       │  │ ClienteController   │  │                │
│       │  │ VentaController     │  │                │
│       │  │ AIController        │  │                │
│       │  └──────────┬──────────┘  │                │
│       └─────────────┼─────────────┘                │
│                     │                               │
│       ┌─────────────▼─────────────┐                │
│       │         DAOs              │                │
│       │  ┌──────────────────────┐ │                │
│       │  │  MedicinaDAO         │ │                │
│       │  │  ClienteDAO          │ │                │
│       │  │  VentaDAO            │ │                │
│       │  └──────────┬───────────┘ │                │
│       └─────────────┼─────────────┘                │
│                     │ JDBC                          │
└─────────────────────┼───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                 MariaDB                             │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │medicina  │ │clientes  │ │ ventas   │          │
│  └──────────┘ └──────────┘ └──────────┘          │
└─────────────────────────────────────────────────────┘

         MÓDULO IA (Separado)
┌─────────────────────────────────────┐
│      Python Scripts                 │
│  ┌──────────────────────────────┐  │
│  │ pharmacy_demand_predictor.py │  │
│  └──────────┬───────────────────┘  │
│             │                        │
│  ┌──────────▼───────────────────┐  │
│  │  Modelos SARIMA (62 .pkl)    │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## 🔐 Seguridad

### Medidas Implementadas

1. **SQL Injection Prevention**
   - Uso de PreparedStatements
   - Validación de entrada

2. **XSS Protection**
   - Sanitización de HTML
   - Escape de caracteres especiales

3. **Validación de Datos**
   - Frontend: JavaScript
   - Backend: Java

4. **Manejo de Errores**
   - Try-catch en todas las operaciones
   - Mensajes de error genéricos al cliente
   - Logging detallado en servidor

---

## ⚡ Optimizaciones

### Performance

1. **Connection Pooling**
   - Reutilización de conexiones BD
   - Reducción de overhead

2. **Índices en BD**
   - Primary keys
   - Foreign keys
   - Índices en campos de búsqueda

3. **Caching**
   - Tipos de medicina en memoria
   - Resultados frecuentes

4. **Lazy Loading**
   - Carga bajo demanda
   - Paginación en listas grandes

---

## 📈 Escalabilidad

### Estrategias para Crecer

1. **Horizontal Scaling**
   - Múltiples instancias de Spark
   - Load balancer (nginx)

2. **Database Scaling**
   - Replicación master-slave
   - Particionamiento de tablas

3. **Microservicios**
   - Separar módulo IA
   - API Gateway

4. **Caching Layer**
   - Redis para datos frecuentes
   - Reducción de carga en BD

---

## 🧪 Testing

### Estrategia de Pruebas

```
Unit Tests (49 tests)
    ↓
Integration Tests
    ↓
E2E Tests
    ↓
Performance Tests
```

Ver: [Documentación de Pruebas](PRUEBAS.md)

---

## 📚 Referencias

- [Spark Framework Docs](https://sparkjava.com/documentation)
- [Gson User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
- [MariaDB Documentation](https://mariadb.com/kb/en/documentation/)
- [Bootstrap 5 Docs](https://getbootstrap.com/docs/5.3/)

---

**Siguiente:** [Documentación API](API.md)