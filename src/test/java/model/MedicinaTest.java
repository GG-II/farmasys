package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class MedicinaTest {
    
    private Medicina medicina;
    
    @BeforeEach
    public void setUp() {
        medicina = new Medicina();
    }
    
    @Test
    public void testSetYGetId() {
        medicina.setId(1);
        assertEquals(1, medicina.getId());
    }
    
    @Test
    public void testSetYGetNombre() {
        medicina.setNombre("ABRILAR");
        assertEquals("ABRILAR", medicina.getNombre());
    }
    
    @Test
    public void testSetYGetPrecio() {
        medicina.setPrecio(150.50);
        assertEquals(150.50, medicina.getPrecio(), 0.01);
    }
    
    @Test
    public void testPrecioNoNegativo() {
        medicina.setPrecio(100.0);
        assertTrue(medicina.getPrecio() >= 0);
    }
    
    @Test
    public void testSetYGetCantidad() {
        medicina.setCantidad(50);
        assertEquals(50, medicina.getCantidad());
    }
    
    @Test
    public void testSetYGetDosis() {
        medicina.setDosis("500mg");
        assertEquals("500mg", medicina.getDosis());
    }
    
    @Test
    public void testSetYGetTipoID() {
        medicina.setTipoID(3);
        assertEquals(3, medicina.getTipoID());
    }
    
    @Test
    public void testSetYGetTipo() {
        medicina.setTipo("TABLETAS");
        assertEquals("TABLETAS", medicina.getTipo());
    }
    
    @Test
    public void testSetYGetDescripcion() {
        medicina.setDescripcion("Medicina para el dolor");
        assertEquals("Medicina para el dolor", medicina.getDescripcion());
    }
    
    @Test
    public void testSetYGetFechaCaducidad() {
        Date fecha = new Date();
        medicina.setFechaCaducidad(fecha);
        assertEquals(fecha, medicina.getFechaCaducidad());
    }
    
    @Test
    public void testConstructorVacio() {
        Medicina med = new Medicina();
        assertNotNull(med);
    }
    
    @Test
    public void testNombreNoNulo() {
        medicina.setNombre("TEST");
        assertNotNull(medicina.getNombre());
    }
}