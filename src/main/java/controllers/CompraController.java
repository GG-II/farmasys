package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.CompraDAO;
import dao.MedicinaDAO;
import model.Compra;
import model.Medicina;
import java.time.LocalDate;
import java.util.List;

public class CompraController {
    private final Gson gson;
    private final CompraDAO compraDAO;
    private final MedicinaDAO medicinaDAO;
    
    public CompraController(Gson gson) {
        this.gson = gson;
        this.compraDAO = new CompraDAO();
        this.medicinaDAO = new MedicinaDAO();
    }
    
    public void registrarRutas() {
        // Obtener compras por período
        get("/api/compras", (req, res) -> {
            res.type("application/json");
            try {
                String periodo = req.queryParams("periodo");
                if (periodo == null) {
                    periodo = "Últimos 30 días";
                }
                List<Compra> compras = compraDAO.obtenerComprasPorPeriodo(periodo);
                return gson.toJson(compras);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al obtener compras: " + e.getMessage()));
            }
        });
        
        // Obtener compra por ID
        get("/api/compras/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Compra compra = compraDAO.obtenerCompraPorId(id);
                
                if (compra != null) {
                    return gson.toJson(compra);
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Compra no encontrada"));
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Registrar nueva compra
        post("/api/compras", (req, res) -> {
            res.type("application/json");
            try {
                Compra compra = gson.fromJson(req.body(), Compra.class);
                
                // Validaciones
                if (compra.getMedicinaId() <= 0) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Debe seleccionar una medicina"));
                }
                
                if (compra.getProveedorId() <= 0) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Debe seleccionar un proveedor"));
                }
                
                if (compra.getCantidad() <= 0) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("La cantidad debe ser mayor a 0"));
                }
                
                // Verificar que la medicina existe
                Medicina medicina = medicinaDAO.obtenerMedicinaPorId(compra.getMedicinaId());
                if (medicina == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Medicina no encontrada"));
                }
                
                // Establecer fecha actual si no viene
                if (compra.getFecha() == null) {
                    compra.setFecha(LocalDate.now());
                }
                
                // Calcular precio total si no viene
                if (compra.getPrecioTotal() == 0) {
                    compra.setPrecioTotal(compra.getPrecioUnidad() * compra.getCantidad());
                }
                
                // Insertar compra
                int idCompra = compraDAO.insertarCompra(compra);
                
                if (idCompra > 0) {
                    // Actualizar stock de la medicina
                    medicina.setCantidad(medicina.getCantidad() + compra.getCantidad());
                    medicinaDAO.actualizarMedicina(medicina);
                    
                    res.status(201);
                    return gson.toJson(new CompraResponse("Compra registrada exitosamente", idCompra));
                } else {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error al registrar la compra"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al registrar compra: " + e.getMessage()));
            }
        });
        
        // Actualizar compra
        put("/api/compras/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Compra compra = gson.fromJson(req.body(), Compra.class);
                compra.setId(id);
                
                // Verificar que existe
                Compra existente = compraDAO.obtenerCompraPorId(id);
                if (existente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Compra no encontrada"));
                }
                
                compraDAO.actualizarCompra(compra);
                return gson.toJson(new SuccessResponse("Compra actualizada exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al actualizar: " + e.getMessage()));
            }
        });
        
        // Eliminar compra
        delete("/api/compras/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                
                // Verificar que existe
                Compra existente = compraDAO.obtenerCompraPorId(id);
                if (existente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Compra no encontrada"));
                }
                
                compraDAO.eliminarCompra(id);
                return gson.toJson(new SuccessResponse("Compra eliminada exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al eliminar: " + e.getMessage()));
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
    
    static class CompraResponse {
        private final String message;
        private final int idCompra;
        
        public CompraResponse(String message, int idCompra) {
            this.message = message;
            this.idCompra = idCompra;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getIdCompra() {
            return idCompra;
        }
    }
}