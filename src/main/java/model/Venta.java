package model;

import java.time.LocalDate;
import java.util.List;

public class Venta {
    private int id;
    private LocalDate fecha;
    private double total;
    private int idCliente;
    private String nombreCliente;
    private List<DetalleVenta> detalles;

    // Constructores
    public Venta() {}

    public Venta(LocalDate fecha, double total, int idCliente) {
        this.fecha = fecha;
        this.total = total;
        this.idCliente = idCliente;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}