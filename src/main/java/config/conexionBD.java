package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionBD {
    private static final String URL = "jdbc:mariadb://127.0.0.1:3306/clinica_farmacia";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin1234";

    // Cargar el driver una sola vez
    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("✅ Driver MariaDB cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MariaDB no encontrado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Crear una NUEVA conexión cada vez que se llame
    public static Connection getConexion() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Nueva conexión establecida");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión SQL: " + e.getMessage());
            throw e;
        }
    }
}