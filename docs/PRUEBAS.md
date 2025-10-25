# 🧪 Guía de Pruebas - FarmaSys

Documentación completa del sistema de pruebas unitarias y de integración.

---

## 📋 Tabla de Contenidos

- [Resumen de Pruebas](#resumen-de-pruebas)
- [Estructura de Tests](#estructura-de-tests)
- [Tests de Modelo](#tests-de-modelo)
- [Tests de DAO](#tests-de-dao)
- [Ejecutar Tests](#ejecutar-tests)
- [Cobertura de Código](#cobertura-de-código)
- [Mejores Prácticas](#mejores-prácticas)

---

## 📊 Resumen de Pruebas

### Estadísticas Generales

```
✅ Total de Tests: 49
✅ Tests Exitosos: 35
⏭️ Tests Deshabilitados: 14 (requieren BD activa)
❌ Fallos: 0
⚠️ Errores: 0
📈 Tasa de Éxito: 100%
```

### Distribución por Tipo

| Tipo de Test | Cantidad | Estado |
|--------------|----------|--------|
| Tests de Modelo | 33 | ✅ Activos |
| Tests de DAO | 16 | ⏭️ Opcionales |
| **TOTAL** | **49** | **100%** |

---

## 🗂️ Estructura de Tests

```
src/test/java/
├── dao/
│   ├── MedicinaDAOTest.java    (9 tests)
│   └── VentaDAOTest.java       (7 tests)
└── model/
    ├── MedicinaTest.java       (12 tests)
    ├── DetalleVentaTest.java   (12 tests)
    └── VentaTest.java          (9 tests)
```

---

## 🏗️ Tests de Modelo

### MedicinaTest.java

**Descripción**: Pruebas unitarias para el modelo `Medicina`

**Tests incluidos**: 12

```java
✅ testSetYGetId()
✅ testSetYGetNombre()
✅ testSetYGetPrecio()
✅ testPrecioNoNegativo()
✅ testSetYGetCantidad()
✅ testSetYGetDosis()
✅ testSetYGetTipoID()
✅ testSetYGetTipo()
✅ testSetYGetDescripcion()
✅ testSetYGetFechaCaducidad()
✅ testConstructorVacio()
✅ testNombreNoNulo()
```

**Ejemplo de Test**:

```java
@Test
public void testSetYGetNombre() {
    medicina.setNombre("ABRILAR");
    assertEquals("ABRILAR", medicina.getNombre());
}

@Test
public void testPrecioNoNegativo() {
    medicina.setPrecio(100.0);
    assertTrue(medicina.getPrecio() >= 0);
}
```

---

### DetalleVentaTest.java

**Descripción**: Pruebas para el modelo `DetalleVenta`

**Tests incluidos**: 12

```java
✅ testSetYGetId()
✅ testSetYGetIdVenta()
✅ testSetYGetIdMedicina()
✅ testSetYGetCantidad()
✅ testSetYGetPrecio()
✅ testSetYGetMedicina()
✅ testGetTotal()
✅ testGetTotalConCero()
✅ testConstructorVacio()
✅ testConstructorConParametros()
✅ testCantidadNoNegativa()
✅ testPrecioNoNegativo()
```

**Test Destacado**:

```java
@Test
public void testGetTotal() {
    detalle.setCantidad(5);
    detalle.setPrecio(20.0);
    
    assertEquals(100.0, detalle.getTotal(), 0.01);
}
```

---

### VentaTest.java

**Descripción**: Pruebas para el modelo `Venta`

**Tests incluidos**: 9

```java
✅ testSetYGetId()
✅ testSetYGetFecha()
✅ testSetYGetTotal()
✅ testSetYGetIdCliente()
✅ testSetYGetNombreCliente()
✅ testSetYGetDetalles()
✅ testConstructorVacio()
✅ testConstructorConParametros()
✅ testTotalNoNegativo()
```

---

## 💾 Tests de DAO

Los tests de DAO están **deshabilitados por defecto** (@Disabled) porque requieren conexión activa a la base de datos.

### MedicinaDAOTest.java

**Tests incluidos**: 9 (8 deshabilitados + 1 activo)

```java
⏭️ testObtenerTodasLasMedicinas()
⏭️ testObtenerMedicinaPorId()
⏭️ testBuscarMedicinasPorNombre()
⏭️ testAgregarMedicina()
⏭️ testActualizarMedicina()
⏭️ testObtenerTiposDeMedicina()
⏭️ testObtenerMedicinasDisponibles()
⏭️ testObtenerMedicinasConPocasExistencias()
✅ testDAOInstancia()
```

**Para habilitar tests de BD**:

1. Asegurar que MariaDB está corriendo
2. Remover `@Disabled` de los tests
3. Ejecutar: `mvn test`

---

### VentaDAOTest.java

**Tests incluidos**: 7 (6 deshabilitados + 1 activo)

```java
⏭️ testObtenerVentasPorPeriodoHoy()
⏭️ testObtenerVentasPorPeriodoTotal()
⏭️ testObtenerVentaPorId()
⏭️ testInsertarVenta()
⏭️ testInsertarDetalleVenta()
⏭️ testObtenerDetallesPorVenta()
✅ testDAOInstancia()
```

---

## 🚀 Ejecutar Tests

### Todos los Tests

```bash
mvn test
```

**Salida esperada**:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dao.MedicinaDAOTest
[INFO] Running dao.VentaDAOTest
[INFO] Running model.DetalleVentaTest
[INFO] Running model.MedicinaTest
[INFO] Running model.VentaTest
[INFO] 
[INFO] Results:
[INFO] Tests run: 49, Failures: 0, Errors: 0, Skipped: 14
[INFO] 
[INFO] BUILD SUCCESS
```

---

### Tests Específicos

#### Una clase específica:
```bash
mvn test -Dtest=MedicinaTest
```

#### Un test específico:
```bash
mvn test -Dtest=MedicinaTest#testSetYGetNombre
```

#### Por paquete:
```bash
# Solo tests de modelo
mvn test -Dtest=model.*

# Solo tests de DAO
mvn test -Dtest=dao.*
```

---

### Con Reporte Detallado

```bash
mvn test
mvn surefire-report:report
```

El reporte se genera en:
```
target/site/surefire-report.html
```

Abre en navegador para ver:
- Resumen de tests
- Tiempo de ejecución
- Stack traces de fallos
- Estadísticas detalladas

---

### Modo Debug

```bash
# Con más información
mvn test -X

# Con logs detallados
mvn test -Dtest=MedicinaTest -Dsurefire.printSummary=true
```

---

## 📈 Cobertura de Código

### Usando JaCoCo (opcional)

Agregar a `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Ejecutar:
```bash
mvn clean test jacoco:report
```

Ver reporte en: `target/site/jacoco/index.html`

---

## 💡 Mejores Prácticas

### 1. Nomenclatura de Tests

```java
// ✅ CORRECTO
@Test
public void testSetYGetNombre()

@Test
public void testPrecioNoNegativo()

// ❌ INCORRECTO
@Test
public void test1()

@Test
public void testear()
```

---

### 2. Usar Assertions Apropiadas

```java
// Para igualdad
assertEquals(expected, actual);

// Para valores null
assertNull(value);
assertNotNull(value);

// Para booleanos
assertTrue(condition);
assertFalse(condition);

// Para números decimales
assertEquals(expected, actual, 0.01);  // Con delta
```

---

### 3. Setup y Teardown

```java
@BeforeEach
public void setUp() {
    medicina = new Medicina();
}

@AfterEach
public void tearDown() {
    medicina = null;
}
```

---

### 4. Tests Independientes

```java
// ✅ CORRECTO - Cada test es independiente
@Test
public void testSetPrecio() {
    Medicina med = new Medicina();
    med.setPrecio(100.0);
    assertEquals(100.0, med.getPrecio());
}

// ❌ INCORRECTO - Depende de otro test
@Test
public void testGetPrecio() {
    // Asume que setPrecio() ya se ejecutó
    assertEquals(100.0, medicina.getPrecio());
}
```

---

### 5. Nombres Descriptivos

```java
// ✅ CORRECTO
@Test
public void testCalcularTotalConDescuento()

// ❌ INCORRECTO
@Test
public void test2()
```

---

## 🐛 Solución de Problemas

### Tests Fallan con "Connection refused"

**Causa**: Base de datos no está corriendo

**Solución**:
```bash
# Iniciar MariaDB
sudo systemctl start mariadb
```

---

### Tests Fallan con "ClassNotFoundException"

**Causa**: Dependencias de test no instaladas

**Solución**:
```bash
mvn clean install
```

---

### Timeout en Tests

**Causa**: Test tarda mucho

**Solución**:
```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
public void testConTimeout() {
    // Test code
}
```

---

## 📝 Agregar Nuevos Tests

### 1. Crear Archivo de Test

```java
package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class NuevaClaseTest {
    
    private NuevaClase objeto;
    
    @BeforeEach
    public void setUp() {
        objeto = new NuevaClase();
    }
    
    @Test
    public void testMetodo() {
        // Arrange
        int expected = 5;
        
        // Act
        int actual = objeto.metodo();
        
        // Assert
        assertEquals(expected, actual);
    }
}
```

### 2. Ubicar en Carpeta Correcta

```
src/test/java/model/NuevaClaseTest.java  (para modelos)
src/test/java/dao/NuevaDAOTest.java      (para DAOs)
```

### 3. Ejecutar

```bash
mvn test -Dtest=NuevaClaseTest
```

---

## 🎯 Checklist de Calidad

Antes de hacer commit, verifica:

- [ ] Todos los tests pasan
- [ ] No hay tests comentados sin razón
- [ ] Tests tienen nombres descriptivos
- [ ] Cobertura > 80% (recomendado)
- [ ] No hay código duplicado en tests
- [ ] Tests son rápidos (< 1 segundo c/u)

---

## 👥 Responsables

**Desarrollo de Tests**: Jeisson Estuardo García Ávila - 0904-21-4886

---

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

## 🔄 Integración Continua

Para configurar CI/CD con GitHub Actions, ver: [CI/CD Guide](CI_CD.md)

---

**¿Encontraste un bug? Escribe un test que lo reproduzca primero! 🐛✅**
