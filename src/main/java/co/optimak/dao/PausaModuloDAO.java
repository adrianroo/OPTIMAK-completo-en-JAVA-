package co.optimak.dao;

import co.optimak.modelo.PausaModulo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PausaModuloDAO {

    public List<PausaModulo> listarPorJornada(long idJornada) {
        List<PausaModulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM PAUSA_MODULO WHERE idJORNADA_MODULO = ? ORDER BY Hora_inicio_pausa";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idJornada);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(construirDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pausas: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    private PausaModulo construirDesdeResultSet(ResultSet rs) throws SQLException {
        PausaModulo p = new PausaModulo();
        p.setIdPausa(rs.getLong("idPAUSA_MODULO"));
        p.setTipoPausa(rs.getString("Tipo_pausa"));
        p.setHoraInicioPausa(rs.getString("Hora_inicio_pausa"));
        p.setDuracionMinutos(rs.getInt("Duracion_minutos"));
        p.setIdJornadaModulo(rs.getLong("idJORNADA_MODULO"));
        return p;
    }
}
