package dao;

import config.conexionBD;
import model.Venta;
import model.DetalleVenta;
import model.Medicina;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    
    public int insertarVenta(Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (fecha, precio, cliente) VALUES (?, ?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDate(1, Date.valueOf(venta.getFecha()));
            pstmt.setDouble(2, venta.getTotal());
            pstmt.setInt(3, venta.getIdCliente());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public void insertarDetalleVenta(DetalleVenta detalle) throws SQLException {
        String sql = "INSERT INTO detalle_ventas (id_venta, id_medicina, cantidad, precio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, detalle.getIdVenta());
            pstmt.setInt(2, detalle.getIdMedicina());
            pstmt.setInt(3, detalle.getCantidad());
            pstmt.setDouble(4, detalle.getPrecio());
            
            pstmt.executeUpdate();
        }
    }
    
    public List<Venta> obtenerVentasPorPeriodo(String periodo) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha, v.precio, v.cliente, " +
                    "CONCAT(c.nombre_1, ' ', c.apellido_1) as nombre_cliente " +
                    "FROM ventas v " +
                    "LEFT JOIN clientes c ON v.cliente = c.id_cliente ";
        
        // Agregar filtro de período
        LocalDate ahora = LocalDate.now();
        if (periodo.equals("Hoy")) {
            sql += "WHERE v.fecha = CURDATE() ";
        } else if (periodo.equals("Últimos 7 días")) {
            sql += "WHERE v.fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ";
        } else if (periodo.equals("Últimos 30 días")) {
            sql += "WHERE v.fecha >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ";
        } else if (periodo.equals("Este mes")) {
            sql += "WHERE MONTH(v.fecha) = MONTH(CURDATE()) AND YEAR(v.fecha) = YEAR(CURDATE()) ";
        }
        
        sql += "ORDER BY v.fecha DESC, v.id_venta DESC";
        
        try (Connection conn = conexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id_venta"));
                venta.setFecha(rs.getDate("fecha").toLocalDate());
                venta.setTotal(rs.getDouble("precio"));
                venta.setIdCliente(rs.getInt("cliente"));
                venta.setNombreCliente(rs.getString("nombre_cliente"));
                ventas.add(venta);
            }
        }
        return ventas;
    }
    
    public Venta obtenerVentaPorId(int id) throws SQLException {
        String sql = "SELECT v.id_venta, v.fecha, v.precio, v.cliente, " +
                    "CONCAT(c.nombre_1, ' ', c.apellido_1) as nombre_cliente " +
                    "FROM ventas v " +
                    "LEFT JOIN clientes c ON v.cliente = c.id_cliente " +
                    "WHERE v.id_venta = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id_venta"));
                venta.setFecha(rs.getDate("fecha").toLocalDate());
                venta.setTotal(rs.getDouble("precio"));
                venta.setIdCliente(rs.getInt("cliente"));
                venta.setNombreCliente(rs.getString("nombre_cliente"));
                
                // Obtener detalles
                venta.setDetalles(obtenerDetallesPorVenta(id));
                
                return venta;
            }
        }
        return null;
    }
    
    public List<DetalleVenta> obtenerDetallesPorVenta(int idVenta) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.id_detalle, dv.id_venta, dv.id_medicina, dv.cantidad, dv.precio, " +
                    "m.nombre, m.dosis " +
                    "FROM detalle_ventas dv " +
                    "INNER JOIN medicina m ON dv.id_medicina = m.id " +
                    "WHERE dv.id_venta = ?";
        
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId(rs.getInt("id_detalle"));
                detalle.setIdVenta(rs.getInt("id_venta"));
                detalle.setIdMedicina(rs.getInt("id_medicina"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecio(rs.getDouble("precio"));
                
                // Crear objeto medicina
                Medicina medicina = new Medicina();
                medicina.setId(rs.getInt("id_medicina"));
                medicina.setNombre(rs.getString("nombre"));
                medicina.setDosis(rs.getString("dosis"));
                detalle.setMedicina(medicina);
                
                detalles.add(detalle);
            }
        }
        return detalles;
    }
}