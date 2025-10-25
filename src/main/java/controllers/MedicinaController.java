package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.MedicinaDAO;
import model.Medicina;
import java.util.List;

public class MedicinaController {
    private final Gson gson;
    private final MedicinaDAO medicinaDAO;
    
    public MedicinaController(Gson gson) {
        this.gson = gson;
        this.medicinaDAO = new MedicinaDAO();
    }
    
    public void registrarRutas() {
        // Obtener todas las medicinas
        get("/api/medicinas", (req, res) -> {
            res.type("application/json");
            try {
                List<Medicina> medicinas = medicinaDAO.obtenerTodasLasMedicinas();
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al obtener medicinas: " + e.getMessage()));
            }
        });
        
        // Obtener medicina por ID
        get("/api/medicinas/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Medicina medicina = medicinaDAO.obtenerMedicinaPorId(id);
                
                if (medicina != null) {
                    return gson.toJson(medicina);
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Medicina no encontrada"));
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Buscar medicinas por nombre
        get("/api/medicinas/buscar/:nombre", (req, res) -> {
            res.type("application/json");
            try {
                String nombre = req.params(":nombre");
                List<Medicina> medicinas = medicinaDAO.buscarMedicinasPorNombre(nombre);
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al buscar: " + e.getMessage()));
            }
        });
        
        // Agregar nueva medicina
        post("/api/medicinas", (req, res) -> {
            res.type("application/json");
            try {
                Medicina medicina = gson.fromJson(req.body(), Medicina.class);
                
                // Validaciones básicas
                if (medicina.getNombre() == null || medicina.getNombre().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("El nombre es obligatorio"));
                }
                
                medicinaDAO.agregarMedicina(medicina);
                res.status(201);
                return gson.toJson(new SuccessResponse("Medicina agregada exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al agregar medicina: " + e.getMessage()));
            }
        });
        
        // Actualizar medicina
        put("/api/medicinas/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Medicina medicina = gson.fromJson(req.body(), Medicina.class);
                medicina.setId(id);
                
                // Verificar que existe
                Medicina existente = medicinaDAO.obtenerMedicinaPorId(id);
                if (existente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Medicina no encontrada"));
                }
                
                medicinaDAO.actualizarMedicina(medicina);
                return gson.toJson(new SuccessResponse("Medicina actualizada exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al actualizar: " + e.getMessage()));
            }
        });
        
        // Obtener tipos de medicina
        get("/api/medicinas/tipos/lista", (req, res) -> {
            res.type("application/json");
            try {
                List<String> tipos = medicinaDAO.obtenerTiposDeMedicina();
                return gson.toJson(tipos);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Obtener ID de tipo por nombre
        get("/api/medicinas/tipos/id/:nombre", (req, res) -> {
            res.type("application/json");
            try {
                String nombre = req.params(":nombre");
                // Mapeo de tipos a IDs (según tu base de datos)
                java.util.Map<String, Integer> tiposMap = new java.util.HashMap<>();
                tiposMap.put("AMPOLLAS", 1);
                tiposMap.put("BLISTER", 2);
                tiposMap.put("TABLETAS", 3);
                tiposMap.put("JARABE", 4);
                tiposMap.put("GEL", 5);
                tiposMap.put("CREMA", 6);
                tiposMap.put("SOBRES", 7);
                tiposMap.put("SPRAY", 8);
                
                Integer id = tiposMap.get(nombre.toUpperCase());
                if (id != null) {
                    return gson.toJson(java.util.Collections.singletonMap("id", id));
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Tipo no encontrado"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Obtener medicinas disponibles (cantidad > 0)
        get("/api/medicinas/disponibles/lista", (req, res) -> {
            res.type("application/json");
            try {
                List<Medicina> medicinas = medicinaDAO.obtenerMedicinasDisponibles();
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Obtener medicinas por vencer
        get("/api/medicinas/por-vencer/lista", (req, res) -> {
            res.type("application/json");
            try {
                List<Medicina> medicinas = medicinaDAO.obtenerMedicinasPorVencer();
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Obtener medicinas con stock bajo
        get("/api/medicinas/stock-bajo/lista", (req, res) -> {
            res.type("application/json");
            try {
                List<Medicina> medicinas = medicinaDAO.obtenerMedicinasConPocasExistencias();
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Obtener medicinas agotadas
        get("/api/medicinas/agotadas/lista", (req, res) -> {
            res.type("application/json");
            try {
                List<Medicina> medicinas = medicinaDAO.obtenerMedicinasConNulaExistencia();
                return gson.toJson(medicinas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
    }
    
    // Clases de respuesta
    static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
    
    static class SuccessResponse {
        private final String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}