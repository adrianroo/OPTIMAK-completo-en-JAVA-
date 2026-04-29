package co.optimak.dao;

import co.optimak.modelo.JornadaModulo;
import co.optimak.modelo.PausaModulo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JornadaModuloDAO {

    /**
     * Busca la jornada de un módulo para una fecha específica.
     * Devuelve null si no existe configuración para esa fecha.
     */
    public JornadaModulo buscarPorModuloYFecha(String moduloId, String empresaNit, String fecha) {
        String sql = "SELECT * FROM JORNADA_MODULO " +
                     "WHERE MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "AND Fecha = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ps.setString(3, fecha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return construirDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar jornada: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return null;
    }

    /**
     * Lista todas las jornadas configuradas para un módulo, ordenadas por fecha desc.
     */
    public List<JornadaModulo> listarPorModulo(String moduloId, String empresaNit) {
        List<JornadaModulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM JORNADA_MODULO " +
                     "WHERE MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "ORDER BY Fecha DESC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(construirDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar jornadas: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Guarda o actualiza la jornada de un módulo para una fecha.
     * Si la jornada ya existe: UPDATE jornada + DELETE pausas antiguas + INSERT pausas nuevas.
     * Si no existe: INSERT jornada + INSERT pausas.
     * Todo en una sola transacción.
     */
    public boolean guardarJornadaConPausas(JornadaModulo jornada, List<PausaModulo> pausas) {
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            JornadaModulo existente = buscarPorModuloYFechaConConexion(
                    jornada.getModuloOperativoId(),
                    jornada.getModuloOperativoEmpresaNit(),
                    jornada.getFecha(),
                    conexion);

            long idJornada;

            if (existente != null) {
                // actualizar jornada existente
                String sqlUpdate = "UPDATE JORNADA_MODULO SET Hora_inicio=?, Hora_fin=?, EsDiaLaborable=? " +
                                   "WHERE idJORNADA_MODULO=?";
                PreparedStatement ps = conexion.prepareStatement(sqlUpdate);
                ps.setString(1, jornada.getHoraInicio());
                ps.setString(2, jornada.getHoraFin());
                ps.setInt(3, jornada.getEsDiaLaborable());
                ps.setLong(4, existente.getIdJornada());
                ps.executeUpdate();
                idJornada = existente.getIdJornada();

                // borrar pausas viejas (CASCADE las borra automáticamente si eliminamos la jornada,
                // pero aquí solo actualizamos la jornada, así que borramos pausas manualmente)
                PreparedStatement psDel = conexion.prepareStatement(
                        "DELETE FROM PAUSA_MODULO WHERE idJORNADA_MODULO = ?");
                psDel.setLong(1, idJornada);
                psDel.executeUpdate();

            } else {
                // insertar jornada nueva
                String sqlInsert = "INSERT INTO JORNADA_MODULO " +
                                   "(Fecha, Hora_inicio, Hora_fin, EsDiaLaborable, " +
                                   "MODULO_OPERATIVO_idMODULO_OPERATIVO, MODULO_OPERATIVO_EMPRESA_NIT) " +
                                   "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, jornada.getFecha());
                ps.setString(2, jornada.getHoraInicio());
                ps.setString(3, jornada.getHoraFin());
                ps.setInt(4, jornada.getEsDiaLaborable());
                ps.setString(5, jornada.getModuloOperativoId());
                ps.setString(6, jornada.getModuloOperativoEmpresaNit());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                idJornada = keys.getLong(1);
            }

            // insertar pausas nuevas
            if (pausas != null && !pausas.isEmpty()) {
                String sqlPausa = "INSERT INTO PAUSA_MODULO " +
                                  "(Tipo_pausa, Hora_inicio_pausa, Duracion_minutos, idJORNADA_MODULO) " +
                                  "VALUES (?, ?, ?, ?)";
                PreparedStatement psPausa = conexion.prepareStatement(sqlPausa);
                for (PausaModulo pausa : pausas) {
                    psPausa.setString(1, pausa.getTipoPausa());
                    psPausa.setString(2, pausa.getHoraInicioPausa());
                    psPausa.setInt(3, pausa.getDuracionMinutos());
                    psPausa.setLong(4, idJornada);
                    psPausa.addBatch();
                }
                psPausa.executeBatch();
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar jornada: " + e.getMessage());
            if (conexion != null) {
                try { conexion.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            return false;
        } finally {
            if (conexion != null) {
                try { conexion.setAutoCommit(true); } catch (SQLException ex) { /* ignorar */ }
            }
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean eliminar(long idJornada) {
        // CASCADE borra las pausas automáticamente
        String sql = "DELETE FROM JORNADA_MODULO WHERE idJORNADA_MODULO = ?";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idJornada);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar jornada: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    private JornadaModulo buscarPorModuloYFechaConConexion(String moduloId, String empresaNit,
                                                            String fecha, Connection conexion)
            throws SQLException {
        String sql = "SELECT * FROM JORNADA_MODULO " +
                     "WHERE MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "AND Fecha = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, moduloId);
        ps.setString(2, empresaNit);
        ps.setString(3, fecha);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return construirDesdeResultSet(rs);
        return null;
    }

    private JornadaModulo construirDesdeResultSet(ResultSet rs) throws SQLException {
        JornadaModulo j = new JornadaModulo();
        j.setIdJornada(rs.getLong("idJORNADA_MODULO"));
        j.setFecha(rs.getString("Fecha"));
        j.setHoraInicio(rs.getString("Hora_inicio"));
        j.setHoraFin(rs.getString("Hora_fin"));
        j.setEsDiaLaborable(rs.getInt("EsDiaLaborable"));
        j.setModuloOperativoId(rs.getString("MODULO_OPERATIVO_idMODULO_OPERATIVO"));
        j.setModuloOperativoEmpresaNit(rs.getString("MODULO_OPERATIVO_EMPRESA_NIT"));
        return j;
    }
}
