package co.optimak.dao;

import co.optimak.modelo.Referencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla REFERENCIA.
 * listarTodas() hace un JOIN con OPERACION para calcular cantOperaciones y totalSam,
 * que son datos derivados utiles para mostrar en la tabla del admin.
 */
public class ReferenciaDAO {

    public List<Referencia> listarTodas() {
        List<Referencia> lista = new ArrayList<>();
        // JOIN para traer el conteo de operaciones y el SAM total de cada referencia
        String sql = "SELECT r.ID_REF, r.Tipo_Prenda, r.Cliente, r.Coleccion, r.Precio_unitario, " +
                     "COUNT(o.ID_operacionREF) AS cantOperaciones, " +
                     "COALESCE(SUM(o.SAM_operacion), 0) AS totalSam " +
                     "FROM REFERENCIA r " +
                     "LEFT JOIN OPERACION o ON o.REFERENCIA_ID_REF = r.ID_REF " +
                     "GROUP BY r.ID_REF, r.Tipo_Prenda, r.Cliente, r.Coleccion, r.Precio_unitario " +
                     "ORDER BY r.ID_REF";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Referencia ref = construirDesdeResultSet(rs);
                ref.setCantOperaciones(rs.getInt("cantOperaciones"));
                ref.setTotalSam(rs.getDouble("totalSam"));
                lista.add(ref);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar referencias: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    public Referencia buscarPorId(String idRef) {
        Referencia ref = null;
        String sql = "SELECT * FROM REFERENCIA WHERE ID_REF = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, idRef);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ref = construirDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar referencia: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return ref;
    }

    public boolean insertar(Referencia ref) {
        String sql = "INSERT INTO REFERENCIA (ID_REF, Tipo_Prenda, Cliente, Coleccion, Precio_unitario) " +
                     "VALUES (?, ?, ?, ?, ?)";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, ref.getIdRef());
            ps.setString(2, ref.getTipoPrenda());
            ps.setString(3, ref.getCliente());
            // coleccion es opcional (NULL permitido)
            if (ref.getColeccion() != null && !ref.getColeccion().trim().isEmpty()) {
                ps.setString(4, ref.getColeccion());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setDouble(5, ref.getPrecioUnitario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar referencia: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean actualizar(Referencia ref) {
        // ID_REF es la PK, no se puede cambiar
        String sql = "UPDATE REFERENCIA SET Tipo_Prenda=?, Cliente=?, Coleccion=?, Precio_unitario=? " +
                     "WHERE ID_REF=?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, ref.getTipoPrenda());
            ps.setString(2, ref.getCliente());
            if (ref.getColeccion() != null && !ref.getColeccion().trim().isEmpty()) {
                ps.setString(3, ref.getColeccion());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setDouble(4, ref.getPrecioUnitario());
            ps.setString(5, ref.getIdRef());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar referencia: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean eliminar(String idRef) {
        // fallará con FK si tiene lotes u operaciones asociadas — el servlet muestra mensaje de error
        String sql = "DELETE FROM REFERENCIA WHERE ID_REF = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, idRef);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar referencia: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    private Referencia construirDesdeResultSet(ResultSet rs) throws SQLException {
        Referencia ref = new Referencia();
        ref.setIdRef(rs.getString("ID_REF"));
        ref.setTipoPrenda(rs.getString("Tipo_Prenda"));
        ref.setCliente(rs.getString("Cliente"));
        ref.setColeccion(rs.getString("Coleccion"));
        ref.setPrecioUnitario(rs.getDouble("Precio_unitario"));
        return ref;
    }
}
