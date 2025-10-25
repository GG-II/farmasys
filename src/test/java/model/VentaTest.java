package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaTest {
    
    private Venta venta;
    
    @BeforeEach
    public void setUp() {
        venta = new Venta();
    }
    
    @Test
    public void testSetYGetId() {
        venta.setId(1);
        assertEquals(1, venta.getId());
    }
    
    @Test
    public void testSetYGetFecha() {
        LocalDate fecha = LocalDate.now();
        venta.setFecha(fecha);
        assertEquals(fecha, venta.getFecha());
    }
    
    @Test
    public void testSetYGetTotal() {
        venta.setTotal(250.50);
        assertEquals(250.50, venta.getTotal(), 0.01);
    }
    
    @Test
    public void testSetYGetIdCliente() {
        venta.setIdCliente(5);
        assertEquals(5, venta.getIdCliente());
    }
    
    @Test
    public void testSetYGetNombreCliente() {
        venta.setNombreCliente("Juan Pérez");
        assertEquals("Juan Pérez", venta.getNombreCliente());
    }
    
    @Test
    public void testSetYGetDetalles() {
        List<DetalleVenta> detalles = new ArrayList<>();
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1);
        detalle.setCantidad(5);
        detalle.setPrecio(50.0);
        detalles.add(detalle);
        
        venta.setDetalles(detalles);
        assertEquals(1, venta.getDetalles().size());
        assertEquals(5, venta.getDetalles().get(0).getCantidad());
    }
    
    @Test
    public void testConstructorVacio() {
        Venta v = new Venta();
        assertNotNull(v);
    }
    
    @Test
    public void testConstructorConParametros() {
        LocalDate fecha = LocalDate.now();
        Venta v = new Venta(fecha, 100.0, 1);
        
        assertEquals(fecha, v.getFecha());
        assertEquals(100.0, v.getTotal(), 0.01);
        assertEquals(1, v.getIdCliente());
    }
    
    @Test
    public void testTotalNoNegativo() {
        venta.setTotal(150.0);
        assertTrue(venta.getTotal() >= 0);
    }
}