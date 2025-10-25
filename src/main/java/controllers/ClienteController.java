package controllers;

import static spark.Spark.*;
import com.google.gson.Gson;
import dao.ClienteDAO;
import model.Cliente;
import java.util.List;

public class ClienteController {
    private final Gson gson;
    private final ClienteDAO clienteDAO;
    
    public ClienteController(Gson gson) {
        this.gson = gson;
        this.clienteDAO = new ClienteDAO();
    }
    
    public void registrarRutas() {
        // Obtener todos los clientes
        get("/api/clientes", (req, res) -> {
            res.type("application/json");
            try {
                List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
                return gson.toJson(clientes);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al obtener clientes: " + e.getMessage()));
            }
        });
        
        // Obtener cliente por ID
        get("/api/clientes/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Cliente cliente = clienteDAO.obtenerClientePorId(id);
                
                if (cliente != null) {
                    return gson.toJson(cliente);
                } else {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Cliente no encontrado"));
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });
        
        // Buscar clientes por nombre
        get("/api/clientes/buscar/:nombre", (req, res) -> {
            res.type("application/json");
            try {
                String nombre = req.params(":nombre");
                List<Cliente> clientes = clienteDAO.buscarClientesPorNombre(nombre);
                return gson.toJson(clientes);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al buscar: " + e.getMessage()));
            }
        });
        
        // Agregar nuevo cliente
        post("/api/clientes", (req, res) -> {
            res.type("application/json");
            try {
                Cliente cliente = gson.fromJson(req.body(), Cliente.class);
                
                // Validaciones básicas
                if (cliente.getPrimerNombre() == null || cliente.getPrimerNombre().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("El primer nombre es obligatorio"));
                }
                
                if (cliente.getPrimerApellido() == null || cliente.getPrimerApellido().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("El primer apellido es obligatorio"));
                }
                
                clienteDAO.agregarCliente(cliente);
                res.status(201);
                return gson.toJson(new SuccessResponse("Cliente agregado exitosamente"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error al agregar cliente: " + e.getMessage()));
            }
        });
        
        // Actualizar cliente
        put("/api/clientes/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Cliente cliente = gson.fromJson(req.body(), Cliente.class);
                cliente.setId(id);
                
                // Verificar que existe
                Cliente existente = clienteDAO.obtenerClientePorId(id);
                if (existente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Cliente no encontrado"));
                }
                
                clienteDAO.actualizarCliente(cliente);
                return gson.toJson(new SuccessResponse("Cliente actualizado exitosamente"));
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