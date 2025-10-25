package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class DetalleVentaTest {
    
    private DetalleVenta detalle;
    
    @BeforeEach
    public void setUp() {
        detalle = new DetalleVenta();
    }
    
    @Test
    public void testSetYGetId() {
        detalle.setId(1);
        assertEquals(1, detalle.getId());
    }
    
    @Test
    public void testSetYGetIdVenta() {
        detalle.setIdVenta(10);
        assertEquals(10, detalle.getIdVenta());
    }
    
    @Test
    public void testSetYGetIdMedicina() {
        detalle.setIdMedicina(5);
        assertEquals(5, detalle.getIdMedicina());
    }
    
    @Test
    public void testSetYGetCantidad() {
        detalle.setCantidad(3);
        assertEquals(3, detalle.getCantidad());
    }
    
    @Test
    public void testSetYGetPrecio() {
        detalle.setPrecio(150.50);
        assertEquals(150.50, detalle.getPrecio(), 0.01);
    }
    
    @Test
    public void testSetYGetMedicina() {
        Medicina medicina = new Medicina();
        medicina.setId(1);
        medicina.setNombre("ABRILAR");
        
        detalle.setMedicina(medicina);
        assertNotNull(detalle.getMedicina());
        assertEquals("ABRILAR", detalle.getMedicina().getNombre());
    }
    
    @Test
    public void testGetTotal() {
        detalle.setCantidad(5);
        detalle.setPrecio(20.0);
        
        assertEquals(100.0, detalle.getTotal(), 0.01);
    }
    
    @Test
    public void testGetTotalConCero() {
        detalle.setCantidad(0);
        detalle.setPrecio(20.0);
        
        assertEquals(0.0, detalle.getTotal(), 0.01);
    }
    
    @Test
    public void testConstructorVacio() {
        DetalleVenta dv = new DetalleVenta();
        assertNotNull(dv);
    }
    
    @Test
    public void testConstructorConParametros() {
        DetalleVenta dv = new DetalleVenta(1, 2, 5, 30.0);
        
        assertEquals(1, dv.getIdVenta());
        assertEquals(2, dv.getIdMedicina());
        assertEquals(5, dv.getCantidad());
        assertEquals(30.0, dv.getPrecio(), 0.01);
        assertEquals(150.0, dv.getTotal(), 0.01);
    }
    
    @Test
    public void testCantidadNoNegativa() {
        detalle.setCantidad(10);
        assertTrue(detalle.getCantidad() >= 0);
    }
    
    @Test
    public void testPrecioNoNegativo() {
        detalle.setPrecio(50.0);
        assertTrue(detalle.getPrecio() >= 0);
    }
}