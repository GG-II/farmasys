package controllers;

import com.google.gson.Gson;
import services.PythonExecutor;
import spark.Request;
import spark.Response;
import java.util.Map;

import static spark.Spark.*;

public class AIController {
    
    private static final Gson gson = new Gson();
    
    public static void registrarRutas() {
        
        // Predecir demanda de una medicina específica
        post("/api/ai/predict", (req, res) -> {
            return predictSingleProduct(req, res);
        });
        
        // Predecir demanda de todas las medicinas
        get("/api/ai/predict-all", (req, res) -> {
            return predictAllProducts(req, res);
        });
        
        System.out.println("  ✓ Módulo IA");
    }
    
    private static String predictSingleProduct(Request req, Response res) {
        res.type("application/json");
        
        try {
            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            
            String productName = (String) body.get("productName");
            int weeksAhead = body.containsKey("weeksAhead") ? 
                ((Double) body.get("weeksAhead")).intValue() : 4;
            
            if (productName == null || productName.isEmpty()) {
                res.status(400);
                return gson.toJson(Map.of(
                    "status", "error",
                    "message", "productName es requerido"
                ));
            }
            
            Map<String, Object> result = PythonExecutor.predictDemand(
                productName, 
                weeksAhead
            );
            
            if ("error".equals(result.get("status"))) {
                res.status(500);
            } else {
                res.status(200);
            }
            
            return gson.toJson(result);
            
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of(
                "status", "error",
                "message", "Error al procesar: " + e.getMessage()
            ));
        }
    }
    
    private static String predictAllProducts(Request req, Response res) {
        res.type("application/json");
        
        try {
            String weeksParam = req.queryParams("weeks");
            int weeksAhead = weeksParam != null ? Integer.parseInt(weeksParam) : 4;
            
            Map<String, Object> result = PythonExecutor.getAllProductsForecast(weeksAhead);
            
            if ("error".equals(result.get("status"))) {
                res.status(500);
            } else {
                res.status(200);
            }
            
            return gson.toJson(result);
            
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage()
            ));
        }
    }
}