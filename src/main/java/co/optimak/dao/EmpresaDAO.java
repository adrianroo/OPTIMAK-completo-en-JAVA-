package co.optimak.dao;

import co.optimak.modelo.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {

    /**
     * Busca la empresa por NIT.
     * El sistema por ahora solo maneja una empresa (MAKAIROS).
     */
    public Empresa buscarPorNit(String nit) {
        String sql = "SELECT * FROM EMPRESA WHERE NIT = ?";
        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setNit(rs.getString("NIT"));
                empresa.setNombre(rs.getString("Nombre"));
                empresa.setDireccion(rs.getString("Direccion"));
                empresa.setCiudad(rs.getString("Ciudad"));
                return empresa;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar empresa: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }

        return null;
    }
}
