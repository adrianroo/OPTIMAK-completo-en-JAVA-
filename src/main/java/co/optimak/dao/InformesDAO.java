package co.optimak.dao;

import co.optimak.modelo.RegistroEficiencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO exclusivo para los informes históricos del administrador.
 * Solo consultas — no inserta ni modifica nada.
 */
public class InformesDAO {

    // =========================================================================
    // TAB 1 — INFORME POR OPERARIO
    // =========================================================================

    /** Registros de eficiencia del operario entre dos fechas, ordenados por tiempo. */
    public List<RegistroEficiencia> registrosEficienciaOperario(String numeroCedula,
                                                                  String fechaDesde,
                                                                  String fechaHasta) {
        List<RegistroEficiencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM REGISTRO_EFICIENCIA " +
                     "WHERE Numero_cedula = ? " +
                     "AND DATE(Periodo_Inicio) BETWEEN ? AND ? " +
                     "ORDER BY Periodo_Inicio ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, fechaDesde);
            ps.setString(3, fechaHasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirRegistroDesdeRS(rs));
        } catch (SQLException e) {
            System.err.println("InformesDAO.registrosEficienciaOperario: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Resumen numérico del operario en el período.
     * Devuelve double[]{promedioEficiencia, totalPulsaciones, totalIncidencias}.
     */
    public double[] resumenOperario(String numeroCedula, String fechaDesde, String fechaHasta) {
        double promedio = 0, pulsaciones = 0, incidencias = 0;

        String sqlProm = "SELECT COALESCE(AVG(EficienciaPorcentaje), 0) " +
                         "FROM REGISTRO_EFICIENCIA " +
                         "WHERE Numero_cedula = ? AND DATE(Periodo_Inicio) BETWEEN ? AND ?";
        String sqlPuls = "SELECT COUNT(*) FROM REGISTRO_PULSACION " +
                         "WHERE Numero_cedula = ? AND DATE(timestamp_pulsacion) BETWEEN ? AND ?";
        String sqlInci = "SELECT COUNT(*) FROM INCIDENCIA i " +
                         "JOIN REGISTRO_EFICIENCIA re ON i.idREGISTRO_EFICIENCIA = re.idREGISTRO_EFICIENCIA " +
                         "WHERE re.Numero_cedula = ? AND DATE(i.FechaHora) BETWEEN ? AND ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();

            PreparedStatement ps1 = conexion.prepareStatement(sqlProm);
            ps1.setString(1, numeroCedula); ps1.setString(2, fechaDesde); ps1.setString(3, fechaHasta);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) promedio = rs1.getDouble(1);

            PreparedStatement ps2 = conexion.prepareStatement(sqlPuls);
            ps2.setString(1, numeroCedula); ps2.setString(2, fechaDesde); ps2.setString(3, fechaHasta);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) pulsaciones = rs2.getInt(1);

            PreparedStatement ps3 = conexion.prepareStatement(sqlInci);
            ps3.setString(1, numeroCedula); ps3.setString(2, fechaDesde); ps3.setString(3, fechaHasta);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) incidencias = rs3.getInt(1);

        } catch (SQLException e) {
            System.err.println("InformesDAO.resumenOperario: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return new double[]{promedio, pulsaciones, incidencias};
    }

    /**
     * Incidencias del operario en el período.
     * Cada fila: Object[]{fechaHora(String), descripcion(String), eficiencia(double)}.
     */
    public List<Object[]> incidenciasOperario(String numeroCedula, String fechaDesde, String fechaHasta) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT i.FechaHora, i.Descripcion, re.EficienciaPorcentaje " +
                     "FROM INCIDENCIA i " +
                     "JOIN REGISTRO_EFICIENCIA re ON i.idREGISTRO_EFICIENCIA = re.idREGISTRO_EFICIENCIA " +
                     "WHERE re.Numero_cedula = ? AND DATE(i.FechaHora) BETWEEN ? AND ? " +
                     "ORDER BY i.FechaHora ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, numeroCedula);
            ps.setString(2, fechaDesde);
            ps.setString(3, fechaHasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("FechaHora"),
                    rs.getString("Descripcion"),
                    rs.getDouble("EficienciaPorcentaje")
                });
            }
        } catch (SQLException e) {
            System.err.println("InformesDAO.incidenciasOperario: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    // =========================================================================
    // TAB 2 — INFORME POR MÓDULO
    // =========================================================================

    /**
     * Eficiencia promedio del módulo por día en el período.
     * Cada fila: Object[]{fecha(String), promedioEficiencia(double), cantOperarios(int)}.
     */
    public List<Object[]> eficienciaDiariaModulo(String moduloId, String empresaNit,
                                                   String fechaDesde, String fechaHasta) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT DATE(re.Periodo_Inicio) as fecha, " +
                     "       ROUND(AVG(re.EficienciaPorcentaje), 2) as promedio, " +
                     "       COUNT(DISTINCT re.Numero_cedula) as cant_operarios " +
                     "FROM REGISTRO_EFICIENCIA re " +
                     "JOIN EMPLEADO_OPERATIVO e ON re.Numero_cedula = e.Numero_cedula " +
                     "WHERE e.MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "  AND e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "  AND DATE(re.Periodo_Inicio) BETWEEN ? AND ? " +
                     "GROUP BY DATE(re.Periodo_Inicio) " +
                     "ORDER BY fecha ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ps.setString(3, fechaDesde);
            ps.setString(4, fechaHasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("fecha"),
                    rs.getDouble("promedio"),
                    rs.getInt("cant_operarios")
                });
            }
        } catch (SQLException e) {
            System.err.println("InformesDAO.eficienciaDiariaModulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Ranking de operarios del módulo en el período, de mayor a menor eficiencia.
     * Cada fila: Object[]{nombre(String), cedula(String), promedioEficiencia(double),
     *                      numRegistros(int), totalProducidas(int)}.
     */
    public List<Object[]> rankingOperariosModulo(String moduloId, String empresaNit,
                                                   String fechaDesde, String fechaHasta) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.Nombre, e.Numero_cedula, " +
                     "       ROUND(AVG(re.EficienciaPorcentaje), 2) as promedio, " +
                     "       COUNT(re.idREGISTRO_EFICIENCIA) as num_registros, " +
                     "       COALESCE(SUM(re.CantidadProducida), 0) as total_producidas " +
                     "FROM REGISTRO_EFICIENCIA re " +
                     "JOIN EMPLEADO_OPERATIVO e ON re.Numero_cedula = e.Numero_cedula " +
                     "WHERE e.MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "  AND e.MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "  AND DATE(re.Periodo_Inicio) BETWEEN ? AND ? " +
                     "GROUP BY e.Numero_cedula, e.Nombre " +
                     "ORDER BY promedio DESC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ps.setString(3, fechaDesde);
            ps.setString(4, fechaHasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("Nombre"),
                    rs.getString("Numero_cedula"),
                    rs.getDouble("promedio"),
                    rs.getInt("num_registros"),
                    rs.getInt("total_producidas")
                });
            }
        } catch (SQLException e) {
            System.err.println("InformesDAO.rankingOperariosModulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    // =========================================================================
    // TAB 3 — INFORME POR LOTE
    // =========================================================================

    /**
     * Avance del lote: pulsaciones acumuladas por cada operación de la referencia.
     * Cada fila: Object[]{nombreCorto(String), samOperacion(double),
     *                      pulsaciones(int), totalUnidades(int), porcentaje(double)}.
     * totalUnidades se pasa como parámetro (viene de Lote.getTotalUnidades()).
     */
    public List<Object[]> avancePorOperacion(String loteOm, String referenciaIdRef, int totalUnidades) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT o.Nombre_corto, o.SAM_operacion, " +
                     "       COUNT(rp.ID_pulsacion) as pulsaciones " +
                     "FROM OPERACION o " +
                     "LEFT JOIN REGISTRO_PULSACION rp " +
                     "  ON o.ID_operacionREF = rp.OPERACION_ID_operacionREF AND rp.LOTE_OM = ? " +
                     "WHERE o.REFERENCIA_ID_REF = ? " +
                     "GROUP BY o.ID_operacionREF, o.Nombre_corto, o.SAM_operacion " +
                     "ORDER BY o.ID_operacionREF ASC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, loteOm);
            ps.setString(2, referenciaIdRef);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pulsaciones = rs.getInt("pulsaciones");
                double porcentaje = totalUnidades > 0
                        ? Math.min(100.0, (pulsaciones * 100.0) / totalUnidades)
                        : 0.0;
                lista.add(new Object[]{
                    rs.getString("Nombre_corto"),
                    rs.getDouble("SAM_operacion"),
                    pulsaciones,
                    totalUnidades,
                    Math.round(porcentaje * 10.0) / 10.0
                });
            }
        } catch (SQLException e) {
            System.err.println("InformesDAO.avancePorOperacion: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Pulsaciones totales por operario en el lote, de mayor a menor.
     * Cada fila: Object[]{nombre(String), cedula(String), pulsaciones(int)}.
     */
    public List<Object[]> pulsacionesPorOperarioEnLote(String loteOm) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.Nombre, e.Numero_cedula, COUNT(rp.ID_pulsacion) as pulsaciones " +
                     "FROM REGISTRO_PULSACION rp " +
                     "JOIN EMPLEADO_OPERATIVO e ON rp.Numero_cedula = e.Numero_cedula " +
                     "WHERE rp.LOTE_OM = ? " +
                     "GROUP BY e.Numero_cedula, e.Nombre " +
                     "ORDER BY pulsaciones DESC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, loteOm);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("Nombre"),
                    rs.getString("Numero_cedula"),
                    rs.getInt("pulsaciones")
                });
            }
        } catch (SQLException e) {
            System.err.println("InformesDAO.pulsacionesPorOperarioEnLote: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    // =========================================================================
    // privado
    // =========================================================================

    private RegistroEficiencia construirRegistroDesdeRS(ResultSet rs) throws SQLException {
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
