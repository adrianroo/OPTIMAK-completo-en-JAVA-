package co.optimak.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO exclusivo del módulo de monitoreo en tiempo real.
 * Solo consultas sobre el turno actual (hoy). No modifica nada.
 */
public class MonitoreoDAO {

    /**
     * Estado general de todos los módulos de la empresa para el turno de hoy.
     * Cada fila: Object[]{
     *   moduloId(String), eficienciaPromedio(double),
     *   operariosActivos(int), totalPulsaciones(int), incidenciasHoy(int)
     * }
     * Incluye módulos sin actividad (eficiencia=0, operariosActivos=0).
     */
    public List<Object[]> estadoModulosHoy(String empresaNit) {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT " +
            "    m.idMODULO_OPERATIVO, " +
            "    COALESCE(ROUND(AVG(re.EficienciaPorcentaje), 2), 0) AS eficiencia_promedio, " +
            "    COUNT(DISTINCT CASE WHEN re.idREGISTRO_EFICIENCIA IS NOT NULL " +
            "          THEN e.Numero_cedula END) AS operarios_activos, " +
            "    ( " +
            "        SELECT COUNT(*) FROM REGISTRO_PULSACION rp " +
            "        JOIN EMPLEADO_OPERATIVO e2 ON rp.Numero_cedula = e2.Numero_cedula " +
            "        WHERE e2.MODULO_OPERATIVO_idMODULO_OPERATIVO = m.idMODULO_OPERATIVO " +
            "          AND e2.MODULO_OPERATIVO_EMPRESA_NIT = m.EMPRESA_NIT " +
            "          AND DATE(rp.timestamp_pulsacion) = CURDATE() " +
            "    ) AS total_pulsaciones, " +
            "    ( " +
            "        SELECT COUNT(*) FROM INCIDENCIA i " +
            "        JOIN REGISTRO_EFICIENCIA re2 ON i.idREGISTRO_EFICIENCIA = re2.idREGISTRO_EFICIENCIA " +
            "        JOIN EMPLEADO_OPERATIVO e3 ON re2.Numero_cedula = e3.Numero_cedula " +
            "        WHERE e3.MODULO_OPERATIVO_idMODULO_OPERATIVO = m.idMODULO_OPERATIVO " +
            "          AND e3.MODULO_OPERATIVO_EMPRESA_NIT = m.EMPRESA_NIT " +
            "          AND DATE(i.FechaHora) = CURDATE() " +
            "    ) AS incidencias_hoy " +
            "FROM MODULO_OPERATIVO m " +
            "LEFT JOIN EMPLEADO_OPERATIVO e " +
            "    ON e.MODULO_OPERATIVO_idMODULO_OPERATIVO = m.idMODULO_OPERATIVO " +
            "    AND e.MODULO_OPERATIVO_EMPRESA_NIT = m.EMPRESA_NIT " +
            "    AND e.Activo = 1 " +
            "LEFT JOIN REGISTRO_EFICIENCIA re " +
            "    ON re.Numero_cedula = e.Numero_cedula " +
            "    AND DATE(re.Periodo_Inicio) = CURDATE() " +
            "WHERE m.EMPRESA_NIT = ? " +
            "GROUP BY m.idMODULO_OPERATIVO " +
            "ORDER BY m.idMODULO_OPERATIVO";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("idMODULO_OPERATIVO"),
                    rs.getDouble("eficiencia_promedio"),
                    rs.getInt("operarios_activos"),
                    rs.getInt("total_pulsaciones"),
                    rs.getInt("incidencias_hoy")
                });
            }
        } catch (SQLException e) {
            System.err.println("MonitoreoDAO.estadoModulosHoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Detalle de los operarios de un módulo para el turno de hoy.
     * Cada fila: Object[]{
     *   nombre(String), cedula(String), eficiencia(double),
     *   pulsacionesHoy(int), loteActual(String — puede ser null)
     * }
     */
    public List<Object[]> detalleOperariosModuloHoy(String moduloId, String empresaNit) {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT " +
            "    e.Nombre, " +
            "    e.Numero_cedula, " +
            "    COALESCE(ROUND(AVG(re.EficienciaPorcentaje), 2), 0) AS eficiencia, " +
            "    ( " +
            "        SELECT COUNT(*) FROM REGISTRO_PULSACION rp " +
            "        WHERE rp.Numero_cedula = e.Numero_cedula " +
            "          AND DATE(rp.timestamp_pulsacion) = CURDATE() " +
            "    ) AS pulsaciones_hoy, " +
            "    ( " +
            "        SELECT rp2.LOTE_OM FROM REGISTRO_PULSACION rp2 " +
            "        WHERE rp2.Numero_cedula = e.Numero_cedula " +
            "        ORDER BY rp2.timestamp_pulsacion DESC LIMIT 1 " +
            "    ) AS lote_actual " +
            "FROM EMPLEADO_OPERATIVO e " +
            "LEFT JOIN REGISTRO_EFICIENCIA re " +
            "    ON re.Numero_cedula = e.Numero_cedula " +
            "    AND DATE(re.Periodo_Inicio) = CURDATE() " +
            "WHERE e.MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
            "  AND e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
            "  AND e.Activo = 1 " +
            "GROUP BY e.Numero_cedula, e.Nombre " +
            "ORDER BY eficiencia DESC, e.Nombre ASC";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("Nombre"),
                    rs.getString("Numero_cedula"),
                    rs.getDouble("eficiencia"),
                    rs.getInt("pulsaciones_hoy"),
                    rs.getString("lote_actual")
                });
            }
        } catch (SQLException e) {
            System.err.println("MonitoreoDAO.detalleOperariosModuloHoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Incidencias registradas hoy en todos los módulos de la empresa.
     * Máximo 20 filas, más recientes primero.
     * Cada fila: Object[]{
     *   fechaHora(String), nombreOperario(String), moduloId(String),
     *   descripcion(String), eficiencia(double)
     * }
     */
    public List<Object[]> incidenciasHoy(String empresaNit) {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT i.FechaHora, e.Nombre, e.MODULO_OPERATIVO_idMODULO_OPERATIVO, " +
            "       i.Descripcion, re.EficienciaPorcentaje " +
            "FROM INCIDENCIA i " +
            "JOIN REGISTRO_EFICIENCIA re ON i.idREGISTRO_EFICIENCIA = re.idREGISTRO_EFICIENCIA " +
            "JOIN EMPLEADO_OPERATIVO e ON re.Numero_cedula = e.Numero_cedula " +
            "WHERE e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
            "  AND DATE(i.FechaHora) = CURDATE() " +
            "ORDER BY i.FechaHora DESC " +
            "LIMIT 20";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("FechaHora"),
                    rs.getString("Nombre"),
                    rs.getString("MODULO_OPERATIVO_idMODULO_OPERATIVO"),
                    rs.getString("Descripcion"),
                    rs.getDouble("EficienciaPorcentaje")
                });
            }
        } catch (SQLException e) {
            System.err.println("MonitoreoDAO.incidenciasHoy: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }
}
