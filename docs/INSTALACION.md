# 📥 Guía de Instalación - FarmaSys

Esta guía te llevará paso a paso por el proceso de instalación completo de FarmaSys.

---

## 📋 Tabla de Contenidos

- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación de Dependencias](#instalación-de-dependencias)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Configuración del Proyecto](#configuración-del-proyecto)
- [Módulo de IA](#módulo-de-ia)
- [Primer Inicio](#primer-inicio)
- [Solución de Problemas](#solución-de-problemas)

---

## 💻 Requisitos del Sistema

### Software Requerido

| Software | Versión Mínima | Recomendada |
|----------|----------------|-------------|
| Java JDK | 11 | 11 o 17 |
| Maven | 3.6.0 | 3.8+ |
| MariaDB/MySQL | 10.3 | 10.6+ |
| Python | 3.8 | 3.10+ |
| Git | 2.0 | Latest |

### Hardware Recomendado

- **CPU**: 2 cores o más
- **RAM**: 4 GB mínimo (8 GB recomendado)
- **Disco**: 2 GB de espacio libre
- **Red**: Conexión a internet para descargar dependencias

---

## 🔧 Instalación de Dependencias

### 1. Instalar Java JDK 11

#### Windows:
```powershell
# Descargar desde Oracle o usar Chocolatey
choco install openjdk11
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

#### Verificar instalación:
```bash
java -version
javac -version
```

---

### 2. Instalar Maven

#### Windows:
```powershell
choco install maven
```

#### Linux:
```bash
sudo apt install maven
```

#### Verificar instalación:
```bash
mvn -version
```

---

### 3. Instalar MariaDB

#### Windows:
1. Descargar desde: https://mariadb.org/download/
2. Ejecutar instalador
3. Configurar root password

#### Linux:
```bash
sudo apt install mariadb-server
sudo mysql_secure_installation
```

#### Iniciar servicio:
```bash
# Windows
net start MySQL

# Linux
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

---

### 4. Instalar Python 3

#### Windows:
```powershell
# Descargar desde python.org o usar Chocolatey
choco install python
```

#### Linux:
```bash
sudo apt install python3 python3-pip
```

#### Verificar instalación:
```bash
python --version
pip --version
```

---

## 🗄️ Configuración de Base de Datos

### 1. Crear Base de Datos

Conectar a MariaDB:
```bash
mysql -u root -p
```

Ejecutar comandos:
```sql
CREATE DATABASE clinica_farmacia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica_farmacia;
```

### 2. Importar Schema

```bash
mysql -u root -p clinica_farmacia < Farmacia.sql
```

### 3. Crear Usuario (Opcional pero recomendado)

```sql
CREATE USER 'farmasys'@'localhost' IDENTIFIED BY 'password_seguro';
GRANT ALL PRIVILEGES ON clinica_farmacia.* TO 'farmasys'@'localhost';
FLUSH PRIVILEGES;
```

### 4. Verificar Tablas

```sql
USE clinica_farmacia;
SHOW TABLES;
```

Deberías ver:
- `medicina`
- `clientes`
- `ventas`
- `detalle_ventas`
- `tipo_medicina`

---

## ⚙️ Configuración del Proyecto

### 1. Clonar Repositorio

```bash
git clone https://github.com/tuusuario/farmasys.git
cd farmasys
```

### 2. Configurar Conexión a BD

Editar: `src/main/java/config/conexionBD.java`

```java
public class conexionBD {
    private static final String URL = "jdbc:mariadb://localhost:3306/clinica_farmacia";
    private static final String USER = "farmasys";  // Tu usuario
    private static final String PASSWORD = "password_seguro";  // Tu contraseña
    
    // ... resto del código
}
```

### 3. Instalar Dependencias Maven

```bash
mvn clean install
```

Esto descargará:
- Spark Framework
- Gson
- MariaDB Driver
- JUnit 5
- Otras dependencias

---

## 🤖 Módulo de IA

### 1. Instalar Dependencias Python

```bash
cd python_ai
pip install -r requirements.txt
```

Si `requirements.txt` no existe, instalar manualmente:

```bash
pip install pandas numpy statsmodels scikit-learn
```

### 2. Preparar Datos de Entrenamiento

Exportar ventas históricas desde la BD:

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

### 3. Entrenar Modelos

```bash
cd python_ai
python train_pharmacy_models.py
```

Esto creará la carpeta `models/` con archivos `.pkl`

### 4. Probar Predictor

```bash
python test_predictor.py
```

---

## 🚀 Primer Inicio

### 1. Compilar Proyecto

```bash
mvn clean compile
```

### 2. Ejecutar Servidor

```bash
mvn exec:java
```

Verás:
```
✓ Servidor iniciado en: http://localhost:4567
✓ Controlador de Medicinas
✓ Controlador de Clientes
✓ Controlador de Ventas
✓ Módulo IA
```

### 3. Acceder a la Aplicación

Abrir navegador:
```
http://localhost:4567
```

### 4. Verificar Funcionalidades

- [ ] Dashboard carga correctamente
- [ ] Ver lista de medicinas
- [ ] Crear un cliente de prueba
- [ ] Registrar una venta
- [ ] Probar predicción IA

---

## 🐛 Solución de Problemas

### Error: "Cannot connect to database"

**Solución:**
1. Verificar que MariaDB está corriendo:
   ```bash
   # Windows
   net start MySQL
   
   # Linux
   sudo systemctl status mariadb
   ```

2. Verificar credenciales en `conexionBD.java`

3. Probar conexión manual:
   ```bash
   mysql -u farmasys -p clinica_farmacia
   ```

---

### Error: "Port 4567 already in use"

**Solución:**
1. Cambiar puerto en `Main.java`:
   ```java
   port(8080);  // Cambiar de 4567 a 8080
   ```

2. O matar proceso en puerto 4567:
   ```bash
   # Windows
   netstat -ano | findstr :4567
   taskkill /PID <PID> /F
   
   # Linux
   lsof -ti:4567 | xargs kill -9
   ```

---

### Error: "Models not found" en módulo IA

**Solución:**
1. Verificar que existe `python_ai/models/`
2. Entrenar modelos:
   ```bash
   cd python_ai
   python train_pharmacy_models.py
   ```

---

### Error: Tests fallan con "class file version"

**Solución:**
1. Limpiar caché de Maven:
   ```bash
   mvn clean
   rmdir /s target  # Windows
   rm -rf target    # Linux
   ```

2. Recompilar:
   ```bash
   mvn compile test
   ```

---

### Python no encuentra módulos

**Solución:**
```bash
# Reinstalar dependencias
pip uninstall pandas numpy statsmodels scikit-learn
pip install pandas numpy statsmodels scikit-learn --upgrade
```

---

## ✅ Verificación de Instalación

Lista de verificación final:

- [ ] Java 11 instalado y configurado
- [ ] Maven funciona correctamente
- [ ] MariaDB corriendo con base de datos creada
- [ ] Python 3 con dependencias instaladas
- [ ] Proyecto compila sin errores
- [ ] Servidor inicia en puerto 4567
- [ ] Frontend accesible desde navegador
- [ ] Módulo IA funciona correctamente
- [ ] Tests pasan exitosamente

---

## 📞 Ayuda Adicional

Si sigues teniendo problemas:

1. Revisa los logs en consola
2. Verifica los archivos en `target/surefire-reports/`
3. Consulta la [Guía de Desarrollo](DESARROLLO.md)
4. Abre un issue en GitHub

---

## 🎓 Siguiente Paso

Una vez instalado, consulta:
- [Arquitectura del Sistema](ARQUITECTURA.md)
- [Documentación API](API.md)
- [Guía de Uso](USER_GUIDE.md)

---

**¿Instalación exitosa? ¡Empieza a usar FarmaSys! 🎉**