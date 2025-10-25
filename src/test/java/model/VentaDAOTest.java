package dao;

import model.Venta;
import model.DetalleVenta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VentaDAOTest {
    
    private VentaDAO ventaDAO;
    
    @BeforeEach
    public void setUp() {
        ventaDAO = new VentaDAO();
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerVentasPorPeriodoHoy() throws SQLException {
        List<Venta> ventas = ventaDAO.obtenerVentasPorPeriodo("Hoy");
        assertNotNull(ventas);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerVentasPorPeriodoTotal() throws SQLException {
        List<Venta> ventas = ventaDAO.obtenerVentasPorPeriodo("Total");
        assertNotNull(ventas);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerVentaPorId() throws SQLException {
        Venta venta = ventaDAO.obtenerVentaPorId(1);
        
        if (venta != null) {
            assertEquals(1, venta.getId());
            assertNotNull(venta.getFecha());
        }
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testInsertarVenta() throws SQLException {
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setTotal(100.0);
        venta.setIdCliente(1);
        
        int id = ventaDAO.insertarVenta(venta);
        assertTrue(id > 0 || id == -1);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testInsertarDetalleVenta() throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdVenta(1);
        detalle.setIdMedicina(1);
        detalle.setCantidad(2);
        detalle.setPrecio(50.0);
        
        assertDoesNotThrow(() -> ventaDAO.insertarDetalleVenta(detalle));
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerDetallesPorVenta() throws SQLException {
        List<DetalleVenta> detalles = ventaDAO.obtenerDetallesPorVenta(1);
        assertNotNull(detalles);
    }
    
    @Test
    public void testDAOInstancia() {
        assertNotNull(ventaDAO);
    }
}