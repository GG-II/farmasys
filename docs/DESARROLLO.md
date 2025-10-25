# 👨‍💻 Guía de Desarrollo - FarmaSys

Documentación para desarrolladores que desean contribuir o extender el proyecto.

---

## 📋 Tabla de Contenidos

- [Equipo de Desarrollo](#equipo-de-desarrollo)
- [Configuración del Entorno](#configuración-del-entorno)
- [Arquitectura del Código](#arquitectura-del-código)
- [Estándares de Codificación](#estándares-de-codificación)
- [Flujo de Trabajo Git](#flujo-de-trabajo-git)
- [Agregar Nuevas Funcionalidades](#agregar-nuevas-funcionalidades)
- [Debugging](#debugging)
- [Contribuir](#contribuir)

---

## 👥 Equipo de Desarrollo

### Desarrolladores Principales

| Nombre | Carné | Rol | Responsabilidad |
|--------|-------|-----|-----------------|
| **Jeisson Estuardo García Ávila** | 0904-21-4886 | Backend Developer | Arquitectura Java, API REST, Base de Datos |
| **Jose Eduardo Alvarado Cartagena** | 0904-21-4561 | ML Engineer | Módulo IA, Modelos SARIMA, Python Integration |
| **Gerbert David García Loaiza** | 0904-21-11608 | Frontend Developer | UI/UX, JavaScript, Diseño Responsive |

---

## 🛠️ Configuración del Entorno

### IDE Recomendados

#### Para Java (Backend):
- **IntelliJ IDEA** (Recomendado)
- Eclipse
- NetBeans

#### Para Python (IA):
- **VS Code** con extensión Python (Recomendado)
- PyCharm
- Jupyter Notebook

#### Para Frontend:
- **VS Code** (Recomendado)
- Sublime Text
- Atom

---

### Plugins Recomendados (VS Code)

```
- Java Extension Pack
- Maven for Java
- Python
- Prettier - Code Formatter
- ESLint
- Live Server
- GitLens
```

---

### Configuración de IntelliJ IDEA

1. **Importar Proyecto Maven**:
   - File → Open → Seleccionar carpeta `farmasys`
   - IntelliJ detectará automáticamente `pom.xml`

2. **Configurar JDK**:
   - File → Project Structure → Project
   - SDK: Java 11

3. **Configurar Maven**:
   - File → Settings → Build Tools → Maven
   - Maven home directory: (auto-detect)

4. **Configurar Base de Datos**:
   - Database → + → Data Source → MariaDB
   - Configurar conexión

---

## 🏗️ Arquitectura del Código

### Backend (Java)

```
src/main/java/
├── Main.java                 # Punto de entrada
├── controllers/              # Controladores REST
│   ├── MedicinaController
│   ├── ClienteController
│   ├── VentaController
│   └── AIController
├── dao/                      # Data Access Objects
│   ├── MedicinaDAO
│   ├── ClienteDAO
│   └── VentaDAO
├── model/                    # Modelos de dominio
│   ├── Medicina
│   ├── Cliente
│   ├── Venta
│   └── DetalleVenta
├── services/                 # Servicios de negocio
│   └── PythonExecutor
└── config/                   # Configuración
    └── conexionBD
```

---

### Frontend (JavaScript)

```
src/main/resources/public/
├── index.html               # Dashboard
├── medicinas.html           # Gestión de medicinas
├── clientes.html            # Gestión de clientes
├── ventas.html              # Sistema de ventas
├── ia.html                  # Predicción IA
├── css/
│   └── styles.css
└── js/
    ├── medicinas.js
    ├── clientes.js
    ├── ventas.js
    └── ia.js
```

---

### Módulo IA (Python)

```
python_ai/
├── pharmacy_demand_predictor.py   # Predictor principal
├── train_pharmacy_models.py       # Entrenamiento
├── models/                        # Modelos entrenados
│   ├── sarima_*.pkl
│   └── sarima_metadata.pkl
├── pharmacy_otc_sales_data.csv   # Datos de entrenamiento
└── requirements.txt              # Dependencias
```

---

## 📏 Estándares de Codificación

### Java (Backend)

#### Nomenclatura:

```java
// Clases: PascalCase
public class MedicinaController { }

// Métodos: camelCase
public void obtenerMedicinas() { }

// Variables: camelCase
private String nombreMedicina;

// Constantes: UPPER_SNAKE_CASE
private static final String API_URL = "...";
```

#### Formato:

```java
// ✅ CORRECTO
public void metodo() {
    if (condicion) {
        // código
    }
}

// ❌ INCORRECTO
public void metodo()
{
    if(condicion)
    {
        // código
    }
}
```

#### Comentarios:

```java
/**
 * Obtiene todas las medicinas de la base de datos
 * 
 * @return Lista de medicinas
 * @throws SQLException si hay error de conexión
 */
public List<Medicina> obtenerTodasLasMedicinas() throws SQLException {
    // Código
}
```

---

### JavaScript (Frontend)

#### Nomenclatura:

```javascript
// Variables y funciones: camelCase
const nombreMedicina = "ABRILAR";
function cargarMedicinas() { }

// Constantes: UPPER_SNAKE_CASE
const API_URL = 'http://localhost:4567/api';

// Clases: PascalCase (si se usan)
class MedicinaManager { }
```

#### Uso de const/let:

```javascript
// ✅ CORRECTO
const API_URL = 'http://localhost:4567/api';
let medicinas = [];

// ❌ INCORRECTO
var API_URL = 'http://localhost:4567/api';
```

#### Async/Await:

```javascript
// ✅ CORRECTO
async function cargarMedicinas() {
    try {
        const response = await fetch(`${API_URL}/medicinas`);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error:', error);
    }
}
```

---

### Python (Módulo IA)

#### Nomenclatura:

```python
# Variables y funciones: snake_case
product_name = "ABRILAR"
def load_models():
    pass

# Clases: PascalCase
class PharmacyDemandPredictor:
    pass

# Constantes: UPPER_SNAKE_CASE
MAX_WEEKS = 12
```

#### Docstrings:

```python
def predict_demand(product_name, weeks_ahead=4):
    """
    Predice la demanda futura de un producto.
    
    Args:
        product_name (str): Nombre del producto
        weeks_ahead (int): Número de semanas a predecir
        
    Returns:
        dict: Resultado de la predicción con intervalos de confianza
    """
    pass
```

---

## 🔄 Flujo de Trabajo Git

### Branching Strategy

```
main
├── develop
│   ├── feature/nueva-funcionalidad
│   ├── feature/modulo-reportes
│   └── bugfix/correccion-ventas
└── hotfix/error-critico
```

---

### Crear Nueva Rama

```bash
# Actualizar develop
git checkout develop
git pull origin develop

# Crear feature branch
git checkout -b feature/nombre-funcionalidad

# Trabajar en la rama
# ... hacer cambios ...

# Commit
git add .
git commit -m "feat: agregar nueva funcionalidad X"

# Push
git push origin feature/nombre-funcionalidad
```

---

### Tipos de Commits (Conventional Commits)

```bash
feat:     # Nueva funcionalidad
fix:      # Corrección de bug
docs:     # Cambios en documentación
style:    # Formato, no afecta lógica
refactor: # Refactorización de código
test:     # Agregar o modificar tests
chore:    # Tareas de mantenimiento
```

**Ejemplos**:

```bash
git commit -m "feat: agregar filtro de búsqueda en medicinas"
git commit -m "fix: corregir cálculo de total en ventas"
git commit -m "docs: actualizar README con instrucciones de IA"
git commit -m "test: agregar tests para MedicinaDAO"
```

---

### Pull Request

1. **Crear PR en GitHub**
2. **Título descriptivo**: "feat: Agregar módulo de reportes"
3. **Descripción detallada**:
   ```markdown
   ## Cambios
   - Agregar endpoint /api/reportes
   - Crear vista reportes.html
   - Implementar gráficos con Chart.js
   
   ## Tests
   - [x] Tests unitarios pasan
   - [x] Tests de integración pasan
   - [x] Probado en navegador
   
   ## Screenshots
   [Adjuntar capturas de pantalla]
   ```

4. **Asignar reviewers**
5. **Esperar aprobación**
6. **Merge a develop**

---

## ➕ Agregar Nuevas Funcionalidades

### 1. Agregar Nuevo Endpoint (Backend)

#### Paso 1: Crear Modelo

```java
// src/main/java/model/NuevoModelo.java
package model;

public class NuevoModelo {
    private int id;
    private String nombre;
    
    // Constructores, getters, setters
}
```

#### Paso 2: Crear DAO

```java
// src/main/java/dao/NuevoDAO.java
package dao;

import model.NuevoModelo;
import java.sql.*;
import java.util.*;

public class NuevoDAO {
    
    public List<NuevoModelo> obtenerTodos() throws SQLException {
        List<NuevoModelo> lista = new ArrayList<>();
        // Implementación
        return lista;
    }
}
```

#### Paso 3: Crear Controlador

```java
// src/main/java/controllers/NuevoController.java
package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.NuevoDAO;

public class NuevoController {
    
    private static final Gson gson = new Gson();
    
    public static void registrarRutas() {
        
        // GET /api/nuevos
        get("/api/nuevos", (req, res) -> {
            res.type("application/json");
            NuevoDAO dao = new NuevoDAO();
            return gson.toJson(dao.obtenerTodos());
        });
        
        // POST /api/nuevos
        post("/api/nuevos", (req, res) -> {
            // Implementación
        });
    }
}
```

#### Paso 4: Registrar en Main.java

```java
import controllers.NuevoController;

public class Main {
    public static void main(String[] args) {
        // ... otros controladores ...
        NuevoController.registrarRutas();
    }
}
```

---

### 2. Agregar Nueva Vista (Frontend)

#### Paso 1: Crear HTML

```html
<!-- src/main/resources/public/nuevo.html -->
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Módulo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1>Nuevo Módulo</h1>
        <div id="contenido"></div>
    </div>
    
    <script src="js/nuevo.js"></script>
</body>
</html>
```

#### Paso 2: Crear JavaScript

```javascript
// src/main/resources/public/js/nuevo.js
const API_URL = 'http://localhost:4567/api';

async function cargarDatos() {
    try {
        const response = await fetch(`${API_URL}/nuevos`);
        const datos = await response.json();
        mostrarDatos(datos);
    } catch (error) {
        console.error('Error:', error);
    }
}

function mostrarDatos(datos) {
    const contenedor = document.getElementById('contenido');
    // Renderizar datos
}

document.addEventListener('DOMContentLoaded', cargarDatos);
```

#### Paso 3: Agregar al Dashboard

```html
<!-- En index.html -->
<a href="nuevo.html" class="btn btn-primary">
    <i class="fas fa-icon"></i> Nuevo Módulo
</a>
```

---

### 3. Extender Módulo IA

#### Agregar Nuevo Algoritmo:

```python
# python_ai/nuevo_predictor.py

import pandas as pd
from sklearn.ensemble import RandomForestRegressor

class NuevoPredictor:
    
    def __init__(self):
        self.model = RandomForestRegressor()
    
    def train(self, X, y):
        self.model.fit(X, y)
    
    def predict(self, X):
        return self.model.predict(X)
```

#### Integrar con Java:

```java
// Agregar método en PythonExecutor.java
public static Map<String, Object> nuevoPredictor(String params) {
    ProcessBuilder pb = new ProcessBuilder(
        "python",
        "python_ai/nuevo_predictor.py",
        params
    );
    // ... resto de la implementación
}
```

---

## 🐛 Debugging

### Backend (Java)

#### IntelliJ IDEA:

1. **Colocar Breakpoints**: Click en línea deseada
2. **Debug Mode**: Run → Debug 'Main'
3. **Inspeccionar Variables**: Ventana Debug
4. **Step Over**: F8
5. **Step Into**: F7

#### Logs:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(MedicinaController.class);

logger.info("Obteniendo medicinas...");
logger.error("Error al procesar: {}", e.getMessage());
```

---

### Frontend (JavaScript)

#### Chrome DevTools:

```javascript
// Console logs
console.log('Datos:', datos);
console.error('Error:', error);
console.table(array);

// Debugger
debugger;  // Pausa ejecución aquí

// Network tab
// Inspeccionar requests HTTP

// Application tab
// Ver localStorage, cookies, etc.
```

---

### Python (Módulo IA)

```python
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

logger.info("Iniciando predicción...")
logger.debug(f"Parámetros: {params}")
logger.error(f"Error: {str(e)}")

# Python debugger
import pdb; pdb.set_trace()
```

---

## 🤝 Contribuir

### Checklist antes de PR

- [ ] Código sigue estándares de proyecto
- [ ] Tests unitarios agregados/actualizados
- [ ] Todos los tests pasan
- [ ] Documentación actualizada
- [ ] No hay console.logs dejados por error
- [ ] Cambios probados manualmente
- [ ] Commits con mensajes descriptivos

---

### Code Review

**Como Reviewer**:
- ✅ Verificar lógica y funcionalidad
- ✅ Buscar bugs potenciales
- ✅ Revisar performance
- ✅ Verificar seguridad
- ✅ Comentar constructivamente

**Como Autor**:
- ✅ Responder a comentarios
- ✅ Hacer cambios solicitados
- ✅ Agradecer feedback
- ✅ Resolver conversaciones

---

## 📚 Recursos de Aprendizaje

### Java & Spark
- [Spark Framework Documentation](https://sparkjava.com/documentation)
- [Effective Java (libro)](https://www.oreilly.com/library/view/effective-java/9780134686097/)

### JavaScript
- [MDN Web Docs](https://developer.mozilla.org/)
- [JavaScript.info](https://javascript.info/)

### Python & ML
- [Scikit-learn Documentation](https://scikit-learn.org/)
- [Statsmodels Documentation](https://www.statsmodels.org/)

### SQL
- [MariaDB Documentation](https://mariadb.org/documentation/)
- [SQL Tutorial](https://www.sqltutorial.org/)

---

## 🎓 Universidad Mariano Gálvez - Huehuetenango

**Facultad**: Ingeniería en Sistemas  
**Curso**: Desarrollo de Software  
**Año**: 2025

---

## 📧 Contacto del Equipo

- **Jeisson García** (Backend)
- **Jose Alvarado** (IA)
- **Gerbert García** (Frontend)

---

**¡Gracias por contribuir a FarmaSys! 💙**
