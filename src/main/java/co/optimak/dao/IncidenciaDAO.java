package co.optimak.dao;

import co.optimak.modelo.Incidencia;

import java.sql.*;

public class IncidenciaDAO {

    /** Inserta una incidencia. FechaHora la pone la BD con DEFAULT CURRENT_TIMESTAMP. */
    public boolean insertar(Incidencia i) {
        String sql = "INSERT INTO INCIDENCIA (Descripcion, Numero_cedula, idREGISTRO_EFICIENCIA) " +
                     "VALUES (?, ?, ?)";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, i.getDescripcion());
            ps.setString(2, i.getNumeroCedula());
            ps.setLong(3, i.getIdRegistroEficiencia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar incidencia: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }
}
