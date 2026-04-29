package co.optimak.dao;

import co.optimak.modelo.RegistroEficiencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroEficienciaDAO {

    /** Inserta un registro y devuelve el ID generado (-1 si falla). */
    public long insertar(RegistroEficiencia r) {
        String sql = "INSERT INTO REGISTRO_EFICIENCIA " +
                     "(Periodo_Inicio, Periodo_Fin, CantidadProducida, EficienciaPorcentaje, " +
                     "Observaciones, Numero_cedula, OM) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getPeriodoInicio());
            ps.setString(2, r.getPeriodoFin());
            ps.setInt(3, r.getCantidadProducida());
            ps.setDouble(4, r.getEficienciaPorcentaje());
            ps.setString(5, r.getObservaciones()); // null si no hay observacion
            ps.setString(6, r.getNumeroCedula());
            ps.setString(7, r.getOm());
            if (ps.executeUpdate() > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar registro de eficiencia: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return -1;
    }

    /**
     * Último registro de eficiencia del operario hoy.
     * El periodoFin de este registro es el "desde" para calcular los próximos 30 min.
     */
    public RegistroEficiencia buscarUltimoDelEmpleadoHoy(String numeroCedula) {
        String sql = "SELECT * FROM REGISTRO_EFICIENCIA " +
                     "WHERE Numero_cedula = ? AND DATE(Periodo_Fin) = CURDATE() " +
                     "ORDER BY Periodo_Fin DESC LIMIT 1";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return construirDesdeResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar último registro de eficiencia: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return null;
    }

    /** Registros del operario en una fecha — para el resumen del día en pantalla. */
    public List<RegistroEficiencia> listarPorEmpleadoYFecha(String numeroCedula, String fecha) {
        List<RegistroEficiencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM REGISTRO_EFICIENCIA " +
                     "WHERE Numero_cedula = ? AND DATE(Periodo_Inicio) = ? " +
                     "ORDER BY Periodo_Inicio ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, fecha);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirDesdeResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar registros de eficiencia: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /** Registros del módulo en una fecha — para monitoreo (módulos futuros). */
    public List<RegistroEficiencia> listarPorModuloYFecha(String moduloId, String empresaNit, String fecha) {
        List<RegistroEficiencia> lista = new ArrayList<>();
        String sql = "SELECT re.* FROM REGISTRO_EFICIENCIA re " +
                     "JOIN EMPLEADO_OPERATIVO e ON re.Numero_cedula = e.Numero_cedula " +
                     "WHERE e.MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "AND DATE(re.Periodo_Inicio) = ? " +
                     "ORDER BY re.Periodo_Inicio ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ps.setString(3, fecha);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirDesdeResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar registros de eficiencia por módulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    private RegistroEficiencia construirDesdeResultSet(ResultSet rs) throws SQLException {
        RegistroEficiencia r = new RegistroEficiencia();
        r.setIdRegistro(rs.getLong("idREGISTRO_EFICIENCIA"));
        r.setPeriodoInicio(rs.getString("Periodo_Inicio"));
        r.setPeriodoFin(rs.getString("Periodo_Fin"));
        r.setCantidadProducida(rs.getInt("CantidadProducida"));
        r.setEficienciaPorcentaje(rs.getDouble("EficienciaPorcentaje"));
        r.setObservaciones(rs.getString("Observaciones"));
        r.setNumeroCedula(rs.getString("Numero_cedula"));
        r.setOm(rs.getString("OM"));
        return r;
    }
}
