package co.optimak.dao;

import co.optimak.modelo.RegistroPulsacion;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroPulsacionDAO {

    /** Inserta una pulsación. El timestamp lo pone la BD con DEFAULT NOW. */
    public boolean insertar(RegistroPulsacion p) {
        String sql = "INSERT INTO REGISTRO_PULSACION " +
                     "(Numero_cedula, OPERACION_ID_operacionREF, LOTE_OM) VALUES (?, ?, ?)";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, p.getNumeroCedula());
            ps.setInt(2, p.getOperacionId());
            ps.setString(3, p.getLoteOm());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar pulsación: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * Cuenta cuántas pulsaciones tiene una operación en un lote (todos los operarios).
     * Usado por ValidadorProduccion para la conciliación de lote.
     */
    public int contarPorOperacionYLote(int operacionId, String loteOm) {
        String sql = "SELECT COUNT(*) FROM REGISTRO_PULSACION " +
                     "WHERE OPERACION_ID_operacionREF = ? AND LOTE_OM = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, operacionId);
            ps.setString(2, loteOm);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar pulsaciones por operación+lote: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return 0;
    }

    /** Cantidad total de pulsaciones del operario hoy. Para mostrar en pantalla. */
    public int contarPorEmpleadoHoy(String numeroCedula, String fecha) {
        String sql = "SELECT COUNT(*) FROM REGISTRO_PULSACION " +
                     "WHERE Numero_cedula = ? AND DATE(timestamp_pulsacion) = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, fecha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar pulsaciones hoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return 0;
    }

    /**
     * Suma los SAM de todas las pulsaciones del operario en una fecha.
     * Resultado = "minutos estándar producidos hoy" → insumo del cálculo de eficiencia.
     */
    public double calcularSamTotalHoy(String numeroCedula, String fecha) {
        String sql = "SELECT COALESCE(SUM(o.SAM_operacion), 0) " +
                     "FROM REGISTRO_PULSACION rp " +
                     "JOIN OPERACION o ON rp.OPERACION_ID_operacionREF = o.ID_operacionREF " +
                     "WHERE rp.Numero_cedula = ? AND DATE(rp.timestamp_pulsacion) = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, fecha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error al calcular SAM total hoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return 0;
    }

    /**
     * SAM total y cantidad de pulsaciones entre dos instantes (para guardar REGISTRO_EFICIENCIA).
     * Devuelve double[]{totalPulsaciones, totalSam}.
     */
    public double[] calcularSamYCountEnPeriodo(String numeroCedula, String desde, String hasta) {
        String sql = "SELECT COUNT(*), COALESCE(SUM(o.SAM_operacion), 0) " +
                     "FROM REGISTRO_PULSACION rp " +
                     "JOIN OPERACION o ON rp.OPERACION_ID_operacionREF = o.ID_operacionREF " +
                     "WHERE rp.Numero_cedula = ? " +
                     "AND rp.timestamp_pulsacion >= ? AND rp.timestamp_pulsacion <= ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, desde);
            ps.setString(3, hasta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new double[]{rs.getDouble(1), rs.getDouble(2)};
        } catch (SQLException e) {
            System.err.println("Error al calcular SAM en período: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return new double[]{0, 0};
    }

    /**
     * SAM total de cada operario del módulo hoy.
     * Se usa para calcular la eficiencia modular.
     * Devuelve Map<numeroCedula, totalSam>.
     */
    public Map<String, Double> obtenerSamPorEmpleadoModuloHoy(String moduloId,
                                                                String empresaNit,
                                                                String fecha) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        String sql = "SELECT rp.Numero_cedula, COALESCE(SUM(o.SAM_operacion), 0) as total_sam " +
                     "FROM REGISTRO_PULSACION rp " +
                     "JOIN OPERACION o ON rp.OPERACION_ID_operacionREF = o.ID_operacionREF " +
                     "JOIN EMPLEADO_OPERATIVO e ON rp.Numero_cedula = e.Numero_cedula " +
                     "WHERE e.MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "AND DATE(rp.timestamp_pulsacion) = ? " +
                     "GROUP BY rp.Numero_cedula";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ps.setString(3, fecha);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener SAM por empleado del módulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return resultado;
    }

    /**
     * Conteo de pulsaciones por operación hoy para un lote.
     * Usado por SistemaAlertas para detectar cuello de botella.
     * Devuelve Map<idOperacion, [count, nombreCorto]>.
     */
    public Map<Integer, Object[]> contarPorOperacionHoyEnLote(String loteOm, String referenciaIdRef) {
        Map<Integer, Object[]> resultado = new LinkedHashMap<>();
        String sql = "SELECT o.ID_operacionREF, o.Nombre_corto, " +
                     "COUNT(rp.ID_pulsacion) as total " +
                     "FROM OPERACION o " +
                     "LEFT JOIN REGISTRO_PULSACION rp " +
                     "  ON o.ID_operacionREF = rp.OPERACION_ID_operacionREF " +
                     "  AND rp.LOTE_OM = ? " +
                     "  AND DATE(rp.timestamp_pulsacion) = CURDATE() " +
                     "WHERE o.REFERENCIA_ID_REF = ? " +
                     "GROUP BY o.ID_operacionREF, o.Nombre_corto";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, loteOm);
            ps.setString(2, referenciaIdRef);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getInt(1), new Object[]{rs.getInt(3), rs.getString(2)});
            }
        } catch (SQLException e) {
            System.err.println("Error al contar pulsaciones por operación hoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return resultado;
    }

    private RegistroPulsacion construirDesdeResultSet(ResultSet rs) throws SQLException {
        RegistroPulsacion p = new RegistroPulsacion();
        p.setIdPulsacion(rs.getLong("ID_pulsacion"));
        p.setTimestampPulsacion(rs.getString("timestamp_pulsacion"));
        p.setNumeroCedula(rs.getString("Numero_cedula"));
        p.setOperacionId(rs.getInt("OPERACION_ID_operacionREF"));
        p.setLoteOm(rs.getString("LOTE_OM"));
        return p;
    }
}
