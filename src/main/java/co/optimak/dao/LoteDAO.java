package co.optimak.dao;

import co.optimak.modelo.Lote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO  para la tabla LOTE,
 * Aquí tengo todos los métodos que hablan con la base de datos
 * Mejor dicho el crud completo
 */
public class LoteDAO {

    /**
     * Traer todos los lotes de mi base de datos:
     */
    public List<Lote> listarTodos() {
        List<Lote> lista = new ArrayList<>();
        String sql = "SELECT * FROM LOTE ORDER BY Fecha_ingreso_planta DESC";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Lote lote = construirLoteDesdeResultSet(rs);
                lista.add(lote);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar lotes: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }

        return lista;
    }

    /**
     * busca un lote por su OM o sea orden maestra.
     */
    public Lote buscarPorOm(String om) {
        Lote lote = null;
        String sql = "SELECT * FROM LOTE WHERE OM = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, om);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lote = construirLoteDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lote por OM: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }

        return lote;
    }

    /**
     * Trae los lotes asignados a un módulo operativo.
     * Usado por SeleccionLoteServlet para mostrar solo los lotes del módulo del operario.
     */
    public List<Lote> listarPorModulo(String moduloId, String empresaNit) {
        List<Lote> lista = new ArrayList<>();
        String sql = "SELECT * FROM LOTE " +
                     "WHERE MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "ORDER BY Fecha_ingreso_planta DESC";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirLoteDesdeResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar lotes por módulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * insertar un nuevo lote en la bd
     */
    public boolean insertar(Lote lote) {
        String sql = "INSERT INTO LOTE (OM, numero_remision_entrada, OC, Color, " +
                     "Cant_XXL, Cant_XL, Cant_L, Cant_M, Cant_S, Cant_XS, " +
                     "Fecha_ingreso_planta, MODULO_OPERATIVO_idMODULO_OPERATIVO, " +
                     "MODULO_OPERATIVO_EMPRESA_NIT, REFERENCIA_ID_REF) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, lote.getOm());
            ps.setString(2, lote.getNumeroRemisionEntrada());
            ps.setString(3, lote.getOc());
            ps.setString(4, lote.getColor());
            ps.setInt(5, lote.getCantXXL());
            ps.setInt(6, lote.getCantXL());
            ps.setInt(7, lote.getCantL());
            ps.setInt(8, lote.getCantM());
            ps.setInt(9, lote.getCantS());
            ps.setInt(10, lote.getCantXS());
            ps.setString(11, lote.getFechaIngresoPlanita());
            ps.setString(12, lote.getModuloOperativoId());
            ps.setString(13, lote.getModuloOperativoEmpresaNit());
            ps.setString(14, lote.getReferenciaIdRef());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar lote: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * actualiza los datos de un lote existente
     * no se puede cambiar la om obviamente porque es la clave primaria
     */
    public boolean actualizar(Lote lote) {
        String sql = "UPDATE LOTE SET numero_remision_entrada = ?, OC = ?, Color = ?, " +
                     "Cant_XXL = ?, Cant_XL = ?, Cant_L = ?, Cant_M = ?, Cant_S = ?, Cant_XS = ?, " +
                     "Fecha_ingreso_planta = ?, MODULO_OPERATIVO_idMODULO_OPERATIVO = ?, " +
                     "MODULO_OPERATIVO_EMPRESA_NIT = ?, REFERENCIA_ID_REF = ? " +
                     "WHERE OM = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, lote.getNumeroRemisionEntrada());
            ps.setString(2, lote.getOc());
            ps.setString(3, lote.getColor());
            ps.setInt(4, lote.getCantXXL());
            ps.setInt(5, lote.getCantXL());
            ps.setInt(6, lote.getCantL());
            ps.setInt(7, lote.getCantM());
            ps.setInt(8, lote.getCantS());
            ps.setInt(9, lote.getCantXS());
            ps.setString(10, lote.getFechaIngresoPlanita());
            ps.setString(11, lote.getModuloOperativoId());
            ps.setString(12, lote.getModuloOperativoEmpresaNit());
            ps.setString(13, lote.getReferenciaIdRef());
            ps.setString(14, lote.getOm());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar lote: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * elimina un lote de la base de datos por su om.
     */
    public boolean eliminar(String om) {
        String sql = "DELETE FROM LOTE WHERE OM = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, om);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar lote: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * metodo privado de apoyo sirve para conviertir una fila del ResultSet en un objeto Lote.
     * para no repetir el mismo código en cada método de consulta.
     */
    private Lote construirLoteDesdeResultSet(ResultSet rs) throws SQLException {
        Lote lote = new Lote();
        lote.setOm(rs.getString("OM"));
        lote.setNumeroRemisionEntrada(rs.getString("numero_remision_entrada"));
        lote.setOc(rs.getString("OC"));
        lote.setColor(rs.getString("Color"));
        lote.setFechaIngresoPlanita(rs.getString("Fecha_ingreso_planta"));
        lote.setReferenciaIdRef(rs.getString("REFERENCIA_ID_REF"));
        lote.setModuloOperativoId(rs.getString("MODULO_OPERATIVO_idMODULO_OPERATIVO"));
        lote.setModuloOperativoEmpresaNit(rs.getString("MODULO_OPERATIVO_EMPRESA_NIT"));
        lote.setCantXXL(rs.getInt("Cant_XXL"));
        lote.setCantXL(rs.getInt("Cant_XL"));
        lote.setCantL(rs.getInt("Cant_L"));
        lote.setCantM(rs.getInt("Cant_M"));
        lote.setCantS(rs.getInt("Cant_S"));
        lote.setCantXS(rs.getInt("Cant_XS"));
        return lote;
    }
}
