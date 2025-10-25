package dao;

import model.Medicina;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MedicinaDAOTest {
    
    private MedicinaDAO medicinaDAO;
    
    @BeforeEach
    public void setUp() {
        medicinaDAO = new MedicinaDAO();
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerTodasLasMedicinas() throws SQLException {
        List<Medicina> medicinas = medicinaDAO.obtenerTodasLasMedicinas();
        assertNotNull(medicinas);
        assertTrue(medicinas.size() >= 0);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerMedicinaPorId() throws SQLException {
        Medicina medicina = medicinaDAO.obtenerMedicinaPorId(1);
        
        if (medicina != null) {
            assertEquals(1, medicina.getId());
            assertNotNull(medicina.getNombre());
        }
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testBuscarMedicinasPorNombre() throws SQLException {
        List<Medicina> medicinas = medicinaDAO.buscarMedicinasPorNombre("ABRILAR");
        assertNotNull(medicinas);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testAgregarMedicina() throws SQLException {
        Medicina medicina = new Medicina();
        medicina.setNombre("TEST_MEDICINA");
        medicina.setDosis("100mg");
        medicina.setPrecio(50.0);
        medicina.setCantidad(10);
        medicina.setTipoID(1);
        medicina.setDescripcion("Medicina de prueba");
        medicina.setFechaCaducidad(new Date());
        
        assertDoesNotThrow(() -> medicinaDAO.agregarMedicina(medicina));
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testActualizarMedicina() throws SQLException {
        Medicina medicina = medicinaDAO.obtenerMedicinaPorId(1);
        
        if (medicina != null) {
            medicina.setCantidad(medicina.getCantidad() + 1);
            assertDoesNotThrow(() -> medicinaDAO.actualizarMedicina(medicina));
        }
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerTiposDeMedicina() throws SQLException {
        List<String> tipos = medicinaDAO.obtenerTiposDeMedicina();
        assertNotNull(tipos);
        assertTrue(tipos.size() > 0);
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerMedicinasDisponibles() throws SQLException {
        List<Medicina> medicinas = medicinaDAO.obtenerMedicinasDisponibles();
        assertNotNull(medicinas);
        
        for (Medicina med : medicinas) {
            assertTrue(med.getCantidad() > 0);
        }
    }
    
    @Test
    @Disabled("Requiere conexión a BD")
    public void testObtenerMedicinasConPocasExistencias() throws SQLException {
        List<Medicina> medicinas = medicinaDAO.obtenerMedicinasConPocasExistencias();
        assertNotNull(medicinas);
        
        for (Medicina med : medicinas) {
            assertTrue(med.getCantidad() > 0 && med.getCantidad() < 10);
        }
    }
    
    @Test
    public void testDAOInstancia() {
        assertNotNull(medicinaDAO);
    }
}