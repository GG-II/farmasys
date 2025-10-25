# 🏥 FarmaSys - Sistema de Gestión de Farmacia con IA

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Spark](https://img.shields.io/badge/Spark-2.9.4-blue.svg)](https://sparkjava.com/)
[![MariaDB](https://img.shields.io/badge/MariaDB-10.x-green.svg)](https://mariadb.org/)
[![Python](https://img.shields.io/badge/Python-3.x-yellow.svg)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-MIT-red.svg)](LICENSE)

Sistema web completo para la gestión de farmacias con módulo de **Inteligencia Artificial** para predicción de demanda usando modelos SARIMA.

---

## 🎯 Características Principales

- ✅ **Gestión de Medicinas**: CRUD completo con control de inventario
- ✅ **Gestión de Clientes**: Registro y búsqueda de clientes
- ✅ **Sistema de Ventas**: Carrito de compras y registro de transacciones
- ✅ **Dashboard Inteligente**: Estadísticas en tiempo real
- ✅ **Predicción con IA**: Forecasting de demanda con Machine Learning
- ✅ **API REST**: Endpoints documentados y seguros
- ✅ **Responsive Design**: Compatible con todos los dispositivos
- ✅ **Pruebas Unitarias**: 49 tests automatizados

---

## 🚀 Demo Rápida

```bash
# Clonar repositorio
git clone https://github.com/tuusuario/farmasys.git
cd farmasys

# Compilar y ejecutar
mvn clean compile
mvn exec:java

# Abrir navegador
http://localhost:4567
```

---

## 📸 Capturas de Pantalla

### Dashboard Principal
![Dashboard](docs/images/dashboard.png)

### Gestión de Medicinas
![Medicinas](docs/images/medicinas.png)

### Predicción con IA
![IA](docs/images/ia-prediction.png)

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 11**: Lenguaje principal
- **Spark Framework 2.9.4**: Servidor web ligero
- **Gson 2.10.1**: Manejo de JSON
- **MariaDB 10.x**: Base de datos relacional
- **Maven**: Gestión de dependencias

### Frontend
- **HTML5 / CSS3**: Estructura y estilos
- **Bootstrap 5.3**: Framework CSS
- **JavaScript ES6**: Lógica del cliente
- **Chart.js**: Gráficos y visualizaciones
- **Font Awesome**: Iconografía

### Módulo IA
- **Python 3.x**: Lenguaje para ML
- **Pandas**: Manipulación de datos
- **Statsmodels**: Modelos SARIMA
- **NumPy**: Operaciones numéricas
- **Scikit-learn**: Métricas y validación

### Testing
- **JUnit 5**: Framework de pruebas
- **Mockito**: Mocking de objetos

---

## 📋 Requisitos Previos

- Java JDK 11 o superior
- Maven 3.6+
- MariaDB 10.x
- Python 3.8+
- Git

---

## 📦 Instalación

### 1. Configurar Base de Datos

```sql
CREATE DATABASE clinica_farmacia;
USE clinica_farmacia;
SOURCE Farmacia.sql;
```

### 2. Configurar Conexión

Editar `src/main/java/config/conexionBD.java`:

```java
private static final String URL = "jdbc:mariadb://localhost:3306/clinica_farmacia";
private static final String USER = "tu_usuario";
private static final String PASSWORD = "tu_contraseña";
```

### 3. Instalar Dependencias Python

```bash
cd python_ai
pip install -r requirements.txt
```

### 4. Compilar y Ejecutar

```bash
mvn clean compile
mvn exec:java
```

El servidor iniciará en: `http://localhost:4567`

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [Instalación Detallada](docs/INSTALACION.md) | Guía paso a paso de instalación |
| [Arquitectura del Sistema](docs/ARQUITECTURA.md) | Diseño y componentes |
| [API REST](docs/API.md) | Documentación de endpoints |
| [Base de Datos](docs/BASE_DE_DATOS.md) | Modelo y esquemas |
| [Frontend](docs/FRONTEND.md) | Estructura del cliente |
| [Módulo IA](docs/MODULO_IA.md) | Machine Learning y predicciones |
| [Pruebas](docs/PRUEBAS.md) | Tests y cobertura |
| [Guía de Desarrollo](docs/DESARROLLO.md) | Contribuir al proyecto |

---

## 🧪 Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=MedicinaTest
mvn test -Dtest=model.*

# Con reporte
mvn surefire-report:report
```

---

## 🤖 Módulo de IA

El sistema incluye un módulo de Inteligencia Artificial que predice la demanda futura de medicamentos usando modelos **SARIMA** (Seasonal AutoRegressive Integrated Moving Average).

### Características:
- Predicción de 1 a 12 semanas
- 62 modelos entrenados (uno por medicina)
- Intervalos de confianza del 95%
- Recomendaciones de inventario automáticas
- Visualización con gráficos interactivos

### Uso:
```bash
cd python_ai
python pharmacy_demand_predictor.py '{"product_name":"ABRILAR","weeks_ahead":4}'
```

Más información: [Documentación del Módulo IA](docs/MODULO_IA.md)

---

## 📊 Estructura del Proyecto

```
farmasys/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controllers/     # Controladores REST
│   │   │   ├── dao/             # Acceso a datos
│   │   │   ├── model/           # Modelos de dominio
│   │   │   ├── services/        # Lógica de negocio
│   │   │   ├── config/          # Configuración
│   │   │   └── Main.java        # Punto de entrada
│   │   └── resources/
│   │       └── public/          # Frontend (HTML/CSS/JS)
│   └── test/
│       └── java/                # Tests unitarios
├── python_ai/
│   ├── models/                  # Modelos ML entrenados
│   ├── pharmacy_demand_predictor.py
│   └── train_pharmacy_models.py
├── docs/                        # Documentación
├── Farmacia.sql                 # Schema de BD
├── pom.xml                      # Maven config
└── README.md
```

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

Ver: [Guía de Desarrollo](docs/DESARROLLO.md)

---

## 👥 Autores

- **Tu Nombre** - *Desarrollo Principal* - [GitHub](https://github.com/tuusuario)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver [LICENSE](LICENSE) para detalles.

---

## 🙏 Agradecimientos

- Universidad Mariano Gálvez de Guatemala
- Comunidad de Spark Framework
- Scikit-learn y Statsmodels

---

## ⭐ Si te gusta este proyecto, dale una estrella en GitHub!

---

**Desarrollado con ❤️ en Guatemala 🇬🇹**