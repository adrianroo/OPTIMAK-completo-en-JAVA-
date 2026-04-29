package co.optimak.dao;

import co.optimak.modelo.Operacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla OPERACION.
 */
public class OperacionDAO {

    public List<Operacion> listarTodas() {
        List<Operacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM OPERACION ORDER BY REFERENCIA_ID_REF, ID_operacionREF";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lista.add(construirDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar operaciones: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    public List<Operacion> listarPorReferencia(String idRef) {
        List<Operacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM OPERACION WHERE REFERENCIA_ID_REF = ? ORDER BY ID_operacionREF";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, idRef);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(construirDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar operaciones por referencia: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    public Operacion buscarPorId(int id) {
        Operacion op = null;
        String sql = "SELECT * FROM OPERACION WHERE ID_operacionREF = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                op = construirDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar operacion: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return op;
    }

    public boolean insertar(Operacion op) {
        // ID_operacionREF es AUTO_INCREMENT, no se inserta
        String sql = "INSERT INTO OPERACION (Nombre_corto, Detalle, Maquina, SAM_operacion, REFERENCIA_ID_REF) " +
                     "VALUES (?, ?, ?, ?, ?)";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, op.getNombreCorto());
            ps.setString(2, op.getDetalle());
            ps.setString(3, op.getMaquina());
            ps.setDouble(4, op.getSamOperacion());
            ps.setString(5, op.getReferenciaIdRef());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar operacion: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean actualizar(Operacion op) {
        String sql = "UPDATE OPERACION SET Nombre_corto=?, Detalle=?, Maquina=?, SAM_operacion=?, " +
                     "REFERENCIA_ID_REF=? WHERE ID_operacionREF=?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, op.getNombreCorto());
            ps.setString(2, op.getDetalle());
            ps.setString(3, op.getMaquina());
            ps.setDouble(4, op.getSamOperacion());
            ps.setString(5, op.getReferenciaIdRef());
            ps.setInt(6, op.getIdOperacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar operacion: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean eliminar(int id) {
        // fallará con FK si tiene registros de pulsación asociados
        String sql = "DELETE FROM OPERACION WHERE ID_operacionREF = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar operacion: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    private Operacion construirDesdeResultSet(ResultSet rs) throws SQLException {
        Operacion op = new Operacion();
        op.setIdOperacion(rs.getInt("ID_operacionREF"));
        op.setNombreCorto(rs.getString("Nombre_corto"));
        op.setDetalle(rs.getString("Detalle"));
        op.setMaquina(rs.getString("Maquina"));
        op.setSamOperacion(rs.getDouble("SAM_operacion"));
        op.setReferenciaIdRef(rs.getString("REFERENCIA_ID_REF"));
        return op;
    }
}
