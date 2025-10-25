package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.VentaDAO;
import dao.MedicinaDAO;
import model.Venta;
import model.DetalleVenta;
import model.Medicina;
import java.time.LocalDate;
import java.util.List;

public class VentaController {
    private final Gson gson;
    private final VentaDAO ventaDAO;
    private final MedicinaDAO medicinaDAO;
    
    public VentaController(Gson gson) {
        this.gson = gson;
        this.ventaDAO = new VentaDAO();
        this.medicinaDAO = new MedicinaDAO();
    }
    
    public void registrarRutas() {
        // Obtener ventas por período
        get("/api/ventas", (req, res) -> {
            res.type("application/json");
            try {
                String periodo = req.queryParams("periodo");
                if (periodo == null) {
                    periodo = "Últimos 30 días";
                }
                List<Venta> ventas = ventaDAO.obtenerVentasPorPeriodo(periodo);
                return gson.toJson(ventas);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al obtener ventas: " + e.getMessage()));
            }
        });
        
        // Obtener venta por ID con detalles
        get("/api/ventas/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Venta venta = ventaDAO.obtenerVentaPorId(id);
                
                if (venta != null) {
                    return gson.toJson(venta);
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Venta no encontrada"));
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Registrar nueva venta con detalles
        post("/api/ventas", (req, res) -> {
            res.type("application/json");
            try {
                Venta venta = gson.fromJson(req.body(), Venta.class);
                
                // Validaciones
                if (venta.getIdCliente() <= 0) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Debe seleccionar un cliente"));
                }
                
                if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Debe agregar al menos un producto"));
                }
                
                // Verificar stock disponible
                for (DetalleVenta detalle : venta.getDetalles()) {
                    Medicina medicina = medicinaDAO.obtenerMedicinaPorId(detalle.getIdMedicina());
                    if (medicina == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Medicina ID " + detalle.getIdMedicina() + " no encontrada"));
                    }
                    if (medicina.getCantidad() < detalle.getCantidad()) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Stock insuficiente para: " + medicina.getNombre()));
                    }
                }
                
                // Establecer fecha actual si no viene
                if (venta.getFecha() == null) {
                    venta.setFecha(LocalDate.now());
                }
                
                // Insertar venta
                int idVenta = ventaDAO.insertarVenta(venta);
                
                if (idVenta > 0) {
                    // Insertar detalles y actualizar stock
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        detalle.setIdVenta(idVenta);
                        ventaDAO.insertarDetalleVenta(detalle);
                        
                        // Actualizar stock de medicina
                        Medicina medicina = medicinaDAO.obtenerMedicinaPorId(detalle.getIdMedicina());
                        medicina.setCantidad(medicina.getCantidad() - detalle.getCantidad());
                        medicinaDAO.actualizarMedicina(medicina);
                    }
                    
                    res.status(201);
                    return gson.toJson(new VentaResponse("Venta registrada exitosamente", idVenta));
                } else {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error al registrar la venta"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al registrar venta: " + e.getMessage()));
            }
        });
        
        // Obtener detalles de una venta
        get("/api/ventas/:id/detalles", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                List<DetalleVenta> detalles = ventaDAO.obtenerDetallesPorVenta(id);
                return gson.toJson(detalles);
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
    
    static class VentaResponse {
        private final String message;
        private final int idVenta;
        
        public VentaResponse(String message, int idVenta) {
            this.message = message;
            this.idVenta = idVenta;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getIdVenta() {
            return idVenta;
        }
    }
}