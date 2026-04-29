package co.optimak.dao;

import co.optimak.modelo.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    /**
     * Busca un administrador por su ID numérico.
     * Devuelve null si no existe.
     */
    public Administrador buscarPorId(int id) {
        String sql = "SELECT * FROM ADMINISTRADOR WHERE idADMINISTRADOR = ?";
        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Administrador admin = new Administrador();
                admin.setIdAdministrador(rs.getInt("idADMINISTRADOR"));
                admin.setNombre(rs.getString("Nombre"));
                admin.setDepartamento(rs.getString("Departamento"));
                admin.setHashContrasena(rs.getString("HashContrasena"));
                admin.setEmpresaNit(rs.getString("EMPRESA_NIT"));
                return admin;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar administrador: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }

        return null;
    }
}
