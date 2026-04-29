package co.optimak.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexionBD {

    // datos de conexión con la base de datos
    // cambiar si tiene otra contraseña en mysql 
    private static final String URL      = "jdbc:mysql://localhost:3306/optimak_db?useSSL=false&serverTimezone=America/Bogota";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "root*";

    public static Connection obtenerConexion() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        System.err.println("Driver MySQL no encontrado: " + e.getMessage());
    }
    return DriverManager.getConnection(URL, USUARIO, PASSWORD);
}
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }
}
