package dao;

import config.conexionBD;
import model.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    
    public List<Proveedor> obtenerTodosLosProveedores() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto FROM proveedor ORDER BY nombre";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setContacto(rs.getString("contacto"));
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }
    
    public Proveedor obtenerProveedorPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, contacto FROM proveedor WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setContacto(rs.getString("contacto"));
                return proveedor;
            }
        }
        return null;
    }
    
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto FROM proveedor WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setContacto(rs.getString("contacto"));
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }
    
    public void agregarProveedor(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, contacto) VALUES (?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getContacto());
            
            pstmt.executeUpdate();
        }
    }
    
    public void actualizarProveedor(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE proveedor SET nombre = ?, contacto = ? WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getContacto());
            pstmt.setInt(3, proveedor.getId());
            
            pstmt.executeUpdate();
        }
    }
}