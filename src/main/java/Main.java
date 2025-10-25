import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import controllers.AIController;

import controllers.MedicinaController;
import controllers.ClienteController;
import controllers.ProveedorController;
import controllers.VentaController;
import controllers.CompraController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .setDateFormat("yyyy-MM-dd")
            .create();

    public static void main(String[] args) {
        // Configurar puerto
        port(4567);
        
        // Configurar archivos estáticos (HTML, CSS, JS)
        staticFiles.location("/public");
        
        // Habilitar CORS para desarrollo
        enableCORS();
        
        System.out.println("========================================");
        System.out.println("🚀 FarmaSys Web Server");
        System.out.println("========================================");
        System.out.println("📡 Servidor iniciado en: http://localhost:4567");
        System.out.println("📊 Dashboard: http://localhost:4567/index.html");
        System.out.println("========================================");
        
        // Registrar controladores
        System.out.println("📦 Registrando módulos...");
        
        MedicinaController medicinaController = new MedicinaController(gson);
        medicinaController.registrarRutas();
        System.out.println("  ✅ Módulo Medicinas");
        
        ClienteController clienteController = new ClienteController(gson);
        clienteController.registrarRutas();
        System.out.println("  ✅ Módulo Clientes");
        
        ProveedorController proveedorController = new ProveedorController(gson);
        proveedorController.registrarRutas();
        System.out.println("  ✅ Módulo Proveedores");
        
        VentaController ventaController = new VentaController(gson);
        ventaController.registrarRutas();
        System.out.println("  ✅ Módulo Ventas");
        
        CompraController compraController = new CompraController(gson);
        compraController.registrarRutas();
        System.out.println("  ✅ Módulo Compras");

        AIController.registrarRutas(); 
        AIController.registrarRutas();
        
        System.out.println("========================================");
        
        // Endpoint de prueba
        get("/api/test", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"ok\",\"message\":\"Servidor FarmaSys funcionando correctamente\",\"version\":\"1.0.0\"}";
        });
        
        // Página de inicio
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });
        
        System.out.println("✅ Sistema listo para usar");
        System.out.println("========================================");
    }
    
    // Habilitar CORS
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }
}