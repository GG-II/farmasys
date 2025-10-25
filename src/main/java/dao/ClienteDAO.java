package dao;

import config.conexionBD;
import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre_1, nombre_2, apellido_1, apellido_2, contacto, genero " +
                    "FROM clientes ORDER BY nombre_1, apellido_1";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setPrimerNombre(rs.getString("nombre_1"));
                cliente.setSegundoNombre(rs.getString("nombre_2"));
                cliente.setPrimerApellido(rs.getString("apellido_1"));
                cliente.setSegundoApellido(rs.getString("apellido_2"));
                cliente.setContacto(rs.getString("contacto"));
                cliente.setGenero(rs.getString("genero"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }
    
    public Cliente obtenerClientePorId(int id) throws SQLException {
        String sql = "SELECT id_cliente, nombre_1, nombre_2, apellido_1, apellido_2, contacto, genero " +
                    "FROM clientes WHERE id_cliente = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setPrimerNombre(rs.getString("nombre_1"));
                cliente.setSegundoNombre(rs.getString("nombre_2"));
                cliente.setPrimerApellido(rs.getString("apellido_1"));
                cliente.setSegundoApellido(rs.getString("apellido_2"));
                cliente.setContacto(rs.getString("contacto"));
                cliente.setGenero(rs.getString("genero"));
                return cliente;
            }
        }
        return null;
    }
    
    public List<Cliente> buscarClientesPorNombre(String nombre) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre_1, nombre_2, apellido_1, apellido_2, contacto, genero " +
                    "FROM clientes " +
                    "WHERE nombre_1 LIKE ? OR apellido_1 LIKE ? OR nombre_2 LIKE ? OR apellido_2 LIKE ? " +
                    "ORDER BY nombre_1, apellido_1";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + nombre + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setPrimerNombre(rs.getString("nombre_1"));
                cliente.setSegundoNombre(rs.getString("nombre_2"));
                cliente.setPrimerApellido(rs.getString("apellido_1"));
                cliente.setSegundoApellido(rs.getString("apellido_2"));
                cliente.setContacto(rs.getString("contacto"));
                cliente.setGenero(rs.getString("genero"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }
    
    public void agregarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre_1, nombre_2, apellido_1, apellido_2, contacto, genero) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getPrimerNombre());
            pstmt.setString(2, cliente.getSegundoNombre());
            pstmt.setString(3, cliente.getPrimerApellido());
            pstmt.setString(4, cliente.getSegundoApellido());
            pstmt.setString(5, cliente.getContacto());
            pstmt.setString(6, cliente.getGenero());
            
            pstmt.executeUpdate();
        }
    }
    
    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre_1 = ?, nombre_2 = ?, apellido_1 = ?, " +
                    "apellido_2 = ?, contacto = ?, genero = ? WHERE id_cliente = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getPrimerNombre());
            pstmt.setString(2, cliente.getSegundoNombre());
            pstmt.setString(3, cliente.getPrimerApellido());
            pstmt.setString(4, cliente.getSegundoApellido());
            pstmt.setString(5, cliente.getContacto());
            pstmt.setString(6, cliente.getGenero());
            pstmt.setInt(7, cliente.getId());
            
            pstmt.executeUpdate();
        }
    }
}