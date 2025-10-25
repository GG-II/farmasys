package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.ProveedorDAO;
import model.Proveedor;
import java.util.List;

public class ProveedorController {
    private final Gson gson;
    private final ProveedorDAO proveedorDAO;
    
    public ProveedorController(Gson gson) {
        this.gson = gson;
        this.proveedorDAO = new ProveedorDAO();
    }
    
    public void registrarRutas() {
        // Obtener todos los proveedores
        get("/api/proveedores", (req, res) -> {
            res.type("application/json");
            try {
                List<Proveedor> proveedores = proveedorDAO.obtenerTodosLosProveedores();
                return gson.toJson(proveedores);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al obtener proveedores: " + e.getMessage()));
            }
        });
        
        // Obtener proveedor por ID
        get("/api/proveedores/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Proveedor proveedor = proveedorDAO.obtenerProveedorPorId(id);
                
                if (proveedor != null) {
                    return gson.toJson(proveedor);
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Proveedor no encontrado"));
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Buscar proveedores por nombre
        get("/api/proveedores/buscar/:nombre", (req, res) -> {
            res.type("application/json");
            try {
                String nombre = req.params(":nombre");
                List<Proveedor> proveedores = proveedorDAO.buscarProveedoresPorNombre(nombre);
                return gson.toJson(proveedores);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al buscar: " + e.getMessage()));
            }
        });
        
        // Agregar nuevo proveedor
        post("/api/proveedores", (req, res) -> {
            res.type("application/json");
            try {
                Proveedor proveedor = gson.fromJson(req.body(), Proveedor.class);
                
                // Validaciones básicas
                if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("El nombre es obligatorio"));
                }
                
                proveedorDAO.agregarProveedor(proveedor);
                res.status(201);
                return gson.toJson(new SuccessResponse("Proveedor agregado exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al agregar proveedor: " + e.getMessage()));
            }
        });
        
        // Actualizar proveedor
        put("/api/proveedores/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Proveedor proveedor = gson.fromJson(req.body(), Proveedor.class);
                proveedor.setId(id);
                
                // Verificar que existe
                Proveedor existente = proveedorDAO.obtenerProveedorPorId(id);
                if (existente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Proveedor no encontrado"));
                }
                
                proveedorDAO.actualizarProveedor(proveedor);
                return gson.toJson(new SuccessResponse("Proveedor actualizado exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al actualizar: " + e.getMessage()));
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