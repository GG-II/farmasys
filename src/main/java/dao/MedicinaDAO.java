package dao;

import config.conexionBD;
import model.Medicina;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicinaDAO {
    
    public List<Medicina> obtenerTodasLasMedicinas() throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "ORDER BY m.nombre";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
    
    public Medicina obtenerMedicinaPorId(int id) throws SQLException {
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                return medicina;
            }
        }
        return null;
    }
    
    public List<Medicina> buscarMedicinasPorNombre(String nombre) throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.nombre LIKE ? " +
                    "ORDER BY m.nombre";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
    
    public void agregarMedicina(Medicina medicina) throws SQLException {
        String sql = "INSERT INTO medicina (nombre, dosis, precio, cantidad, fecha_caducidad, descripcion, tipo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medicina.getNombre());
            pstmt.setString(2, medicina.getDosis());
            pstmt.setDouble(3, medicina.getPrecio());
            pstmt.setInt(4, medicina.getCantidad());
            pstmt.setDate(5, medicina.getFechaCaducidad() != null ? 
                new java.sql.Date(medicina.getFechaCaducidad().getTime()) : null);
            pstmt.setString(6, medicina.getDescripcion());
            pstmt.setInt(7, medicina.getTipoID());
            
            pstmt.executeUpdate();
        }
    }
    
    public void actualizarMedicina(Medicina medicina) throws SQLException {
        String sql = "UPDATE medicina SET nombre = ?, dosis = ?, precio = ?, cantidad = ?, " +
                    "fecha_caducidad = ?, descripcion = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medicina.getNombre());
            pstmt.setString(2, medicina.getDosis());
            pstmt.setDouble(3, medicina.getPrecio());
            pstmt.setInt(4, medicina.getCantidad());
            pstmt.setDate(5, medicina.getFechaCaducidad() != null ? 
                new java.sql.Date(medicina.getFechaCaducidad().getTime()) : null);
            pstmt.setString(6, medicina.getDescripcion());
            pstmt.setInt(7, medicina.getTipoID());
            pstmt.setInt(8, medicina.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public List<String> obtenerTiposDeMedicina() throws SQLException {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT tipo_medicina FROM tipo_medicina ORDER BY id_tipo";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tipos.add(rs.getString("tipo_medicina"));
            }
        }
        return tipos;
    }
    
    public List<Medicina> obtenerMedicinasDisponibles() throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.cantidad > 0 " +
                    "ORDER BY m.nombre";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
    
    public List<Medicina> obtenerMedicinasPorVencer() throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.fecha_caducidad BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 60 DAY) " +
                    "ORDER BY m.fecha_caducidad";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
    
    public List<Medicina> obtenerMedicinasConPocasExistencias() throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.cantidad > 0 AND m.cantidad < 10 " +
                    "ORDER BY m.cantidad";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
    
    public List<Medicina> obtenerMedicinasConNulaExistencia() throws SQLException {
        List<Medicina> medicinas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.dosis, m.precio, m.cantidad, m.fecha_caducidad, " +
                    "m.descripcion, m.tipo, t.tipo_medicina " +
                    "FROM medicina m " +
                    "LEFT JOIN tipo_medicina t ON m.tipo = t.id_tipo " +
                    "WHERE m.cantidad = 0 " +
                    "ORDER BY m.nombre";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                medicina.setPrecio(rs.getDouble("precio"));
                medicina.setCantidad(rs.getInt("cantidad"));
                medicina.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                medicina.setDescripcion(rs.getString("descripcion"));
                medicina.setTipoID(rs.getInt("tipo"));
                medicina.setTipo(rs.getString("tipo_medicina"));
                medicinas.add(medicina);
            }
        }
        return medicinas;
    }
}