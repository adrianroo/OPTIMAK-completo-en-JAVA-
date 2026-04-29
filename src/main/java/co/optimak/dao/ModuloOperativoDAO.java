package co.optimak.dao;

import co.optimak.modelo.ModuloOperativo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuloOperativoDAO {

    public List<ModuloOperativo> listarTodos() {
        List<ModuloOperativo> lista = new ArrayList<>();
        String sql = "SELECT * FROM MODULO_OPERATIVO ORDER BY idMODULO_OPERATIVO";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ModuloOperativo mod = new ModuloOperativo();
                mod.setIdModulo(rs.getString("idMODULO_OPERATIVO"));
                mod.setEmpresaNit(rs.getString("EMPRESA_NIT"));
                lista.add(mod);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar módulos: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Lista todos los módulos de la empresa junto con el conteo de operarios activos.
     * Usa LEFT JOIN para incluir módulos sin operarios.
     */
    public List<ModuloOperativo> listarConConteoOperarios(String empresaNit) {
        List<ModuloOperativo> lista = new ArrayList<>();
        String sql = "SELECT m.idMODULO_OPERATIVO, m.EMPRESA_NIT, " +
                     "COUNT(e.Numero_cedula) AS cantOperarios " +
                     "FROM MODULO_OPERATIVO m " +
                     "LEFT JOIN EMPLEADO_OPERATIVO e " +
                     "  ON e.MODULO_OPERATIVO_idMODULO_OPERATIVO = m.idMODULO_OPERATIVO " +
                     "  AND e.MODULO_OPERATIVO_EMPRESA_NIT = m.EMPRESA_NIT " +
                     "  AND e.Activo = 1 " +
                     "WHERE m.EMPRESA_NIT = ? " +
                     "GROUP BY m.idMODULO_OPERATIVO, m.EMPRESA_NIT " +
                     "ORDER BY m.idMODULO_OPERATIVO";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ModuloOperativo mod = new ModuloOperativo();
                mod.setIdModulo(rs.getString("idMODULO_OPERATIVO"));
                mod.setEmpresaNit(rs.getString("EMPRESA_NIT"));
                mod.setCantOperarios(rs.getInt("cantOperarios"));
                lista.add(mod);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar módulos con conteo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    public boolean insertar(String idModulo, String empresaNit) {
        String sql = "INSERT INTO MODULO_OPERATIVO (idMODULO_OPERATIVO, EMPRESA_NIT) VALUES (?, ?)";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, idModulo.trim().toUpperCase());
            ps.setString(2, empresaNit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar módulo: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * Elimina un módulo. Fallará si tiene empleados o lotes asignados (FK restriction).
     */
    public boolean eliminar(String idModulo, String empresaNit) {
        String sql = "DELETE FROM MODULO_OPERATIVO WHERE idMODULO_OPERATIVO = ? AND EMPRESA_NIT = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, idModulo);
            ps.setString(2, empresaNit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar módulo: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }
}
