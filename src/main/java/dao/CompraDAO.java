package dao;

import config.conexionBD;
import model.Compra;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {
    
    public int insertarCompra(Compra compra) throws SQLException {
        String sql = "INSERT INTO compra (fecha, medicina_id, cantidad, precio_unidad, precio_total, proveedor_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDate(1, Date.valueOf(compra.getFecha()));
            pstmt.setInt(2, compra.getMedicinaId());
            pstmt.setInt(3, compra.getCantidad());
            pstmt.setDouble(4, compra.getPrecioUnidad());
            pstmt.setDouble(5, compra.getPrecioTotal());
            pstmt.setInt(6, compra.getProveedorId());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Compra> obtenerComprasPorPeriodo(String periodo) throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT id, fecha, medicina_id, cantidad, precio_unidad, precio_total, proveedor_id " +
                    "FROM compra ";
        
        // Agregar filtro de período
        if (periodo.equals("Hoy")) {
            sql += "WHERE fecha = CURDATE() ";
        } else if (periodo.equals("Últimos 7 días")) {
            sql += "WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ";
        } else if (periodo.equals("Últimos 30 días")) {
            sql += "WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ";
        } else if (periodo.equals("Este mes")) {
            sql += "WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE()) ";
        }
        
        sql += "ORDER BY fecha DESC, id DESC";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id"));
                compra.setFecha(rs.getDate("fecha").toLocalDate());
                compra.setMedicinaId(rs.getInt("medicina_id"));
                compra.setCantidad(rs.getInt("cantidad"));
                compra.setPrecioUnidad(rs.getDouble("precio_unidad"));
                compra.setPrecioTotal(rs.getDouble("precio_total"));
                compra.setProveedorId(rs.getInt("proveedor_id"));
                compras.add(compra);
            }
        }
        return compras;
    }
    
    public Compra obtenerCompraPorId(int id) throws SQLException {
        String sql = "SELECT id, fecha, medicina_id, cantidad, precio_unidad, precio_total, proveedor_id " +
                    "FROM compra WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id"));
                compra.setFecha(rs.getDate("fecha").toLocalDate());
                compra.setMedicinaId(rs.getInt("medicina_id"));
                compra.setCantidad(rs.getInt("cantidad"));
                compra.setPrecioUnidad(rs.getDouble("precio_unidad"));
                compra.setPrecioTotal(rs.getDouble("precio_total"));
                compra.setProveedorId(rs.getInt("proveedor_id"));
                return compra;
            }
        }
        return null;
    }
    
    public void actualizarCompra(Compra compra) throws SQLException {
        String sql = "UPDATE compra SET fecha = ?, medicina_id = ?, cantidad = ?, " +
                    "precio_unidad = ?, precio_total = ?, proveedor_id = ? WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(compra.getFecha()));
            pstmt.setInt(2, compra.getMedicinaId());
            pstmt.setInt(3, compra.getCantidad());
            pstmt.setDouble(4, compra.getPrecioUnidad());
            pstmt.setDouble(5, compra.getPrecioTotal());
            pstmt.setInt(6, compra.getProveedorId());
            pstmt.setInt(7, compra.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void eliminarCompra(int id) throws SQLException {
        String sql = "DELETE FROM compra WHERE id = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}