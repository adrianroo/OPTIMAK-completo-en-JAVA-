package co.optimak.dao;

import co.optimak.modelo.EmpleadoOperativo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public EmpleadoOperativo buscarPorCedula(String cedula) {
        String sql = "SELECT * FROM EMPLEADO_OPERATIVO WHERE Numero_cedula = ? AND Activo = 1";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return construirDesdeResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar empleado por cédula: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return null;
    }

    public List<EmpleadoOperativo> listarActivosPorModulo(String moduloId, String empresaNit) {
        List<EmpleadoOperativo> lista = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADO_OPERATIVO " +
                     "WHERE MODULO_OPERATIVO_idMODULO_OPERATIVO = ? " +
                     "AND MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                     "AND Activo = 1 ORDER BY Nombre";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, moduloId);
            ps.setString(2, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirDesdeResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar empleados por módulo: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    public List<EmpleadoOperativo> listarTodosActivos(String empresaNit) {
        List<EmpleadoOperativo> lista = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADO_OPERATIVO " +
                     "WHERE MODULO_OPERATIVO_EMPRESA_NIT = ? AND Activo = 1 ORDER BY Nombre";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, empresaNit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(construirDesdeResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar empleados activos: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
        return lista;
    }

    /**
     * Inserta un nuevo empleado operativo.
     * También crea su primer registro en HISTORIAL_MODULO_EMPLEADO con FechaFin=NULL.
     * Todo en una transacción para garantizar consistencia.
     */
    public boolean insertar(EmpleadoOperativo emp) {
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            String sqlEmp = "INSERT INTO EMPLEADO_OPERATIVO " +
                            "(Numero_cedula, Nombre, Telefono, Edad, Sexo, Cargo_rol, Correo, " +
                            "HashContrasena, Activo, " +
                            "MODULO_OPERATIVO_idMODULO_OPERATIVO, MODULO_OPERATIVO_EMPRESA_NIT) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sqlEmp);
            ps.setString(1, emp.getNumeroCedula());
            ps.setString(2, emp.getNombre());
            setNullable(ps, 3, emp.getTelefono());
            ps.setInt(4, emp.getEdad());
            ps.setString(5, emp.getSexo());
            ps.setString(6, emp.getCargoRol());
            setNullable(ps, 7, emp.getCorreo());
            ps.setString(8, emp.getHashContrasena());
            ps.setString(9, emp.getModuloOperativoId());
            ps.setString(10, emp.getModuloOperativoEmpresaNit());
            ps.executeUpdate();

            // registrar la asignación inicial en el historial
            String sqlHistorial = "INSERT INTO HISTORIAL_MODULO_EMPLEADO " +
                                  "(FechaInicio, FechaFin, Numero_cedula, " +
                                  "MODULO_OPERATIVO_idMODULO_OPERATIVO, MODULO_OPERATIVO_EMPRESA_NIT) " +
                                  "VALUES (?, NULL, ?, ?, ?)";
            PreparedStatement psH = conexion.prepareStatement(sqlHistorial);
            psH.setString(1, LocalDate.now().toString());
            psH.setString(2, emp.getNumeroCedula());
            psH.setString(3, emp.getModuloOperativoId());
            psH.setString(4, emp.getModuloOperativoEmpresaNit());
            psH.executeUpdate();

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            rollback(conexion);
            return false;
        } finally {
            resetAutoCommit(conexion);
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * Actualiza los datos básicos del empleado (sin cambio de módulo).
     * Para cambiar de módulo usar cambiarModulo().
     */
    public boolean actualizarDatos(EmpleadoOperativo emp) {
        String sql = "UPDATE EMPLEADO_OPERATIVO SET Nombre=?, Telefono=?, Edad=?, Sexo=?, " +
                     "Cargo_rol=?, Correo=? WHERE Numero_cedula=? AND Activo=1";
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, emp.getNombre());
            setNullable(ps, 2, emp.getTelefono());
            ps.setInt(3, emp.getEdad());
            ps.setString(4, emp.getSexo());
            ps.setString(5, emp.getCargoRol());
            setNullable(ps, 6, emp.getCorreo());
            ps.setString(7, emp.getNumeroCedula());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar datos del empleado: " + e.getMessage());
            return false;
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * Cambia de módulo a un operario.
     * Transacción de 3 pasos obligatorios:
     * 1. Cierra el registro activo en HISTORIAL_MODULO_EMPLEADO (FechaFin = hoy).
     * 2. Inserta nuevo registro en HISTORIAL_MODULO_EMPLEADO con FechaFin = NULL.
     * 3. Actualiza la FK directa en EMPLEADO_OPERATIVO.
     */
    public boolean cambiarModulo(String cedula, String nuevoModuloId, String empresaNit) {
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            String hoy = LocalDate.now().toString();

            // paso 1: cerrar el registro activo del historial
            String sqlCerrar = "UPDATE HISTORIAL_MODULO_EMPLEADO SET FechaFin = ? " +
                               "WHERE Numero_cedula = ? AND FechaFin IS NULL";
            PreparedStatement psCerrar = conexion.prepareStatement(sqlCerrar);
            psCerrar.setString(1, hoy);
            psCerrar.setString(2, cedula);
            psCerrar.executeUpdate();

            // paso 2: insertar nuevo registro en el historial
            String sqlNuevo = "INSERT INTO HISTORIAL_MODULO_EMPLEADO " +
                              "(FechaInicio, FechaFin, Numero_cedula, " +
                              "MODULO_OPERATIVO_idMODULO_OPERATIVO, MODULO_OPERATIVO_EMPRESA_NIT) " +
                              "VALUES (?, NULL, ?, ?, ?)";
            PreparedStatement psNuevo = conexion.prepareStatement(sqlNuevo);
            psNuevo.setString(1, hoy);
            psNuevo.setString(2, cedula);
            psNuevo.setString(3, nuevoModuloId);
            psNuevo.setString(4, empresaNit);
            psNuevo.executeUpdate();

            // paso 3: actualizar la FK directa del empleado
            String sqlEmp = "UPDATE EMPLEADO_OPERATIVO " +
                            "SET MODULO_OPERATIVO_idMODULO_OPERATIVO = ?, " +
                            "MODULO_OPERATIVO_EMPRESA_NIT = ? " +
                            "WHERE Numero_cedula = ?";
            PreparedStatement psEmp = conexion.prepareStatement(sqlEmp);
            psEmp.setString(1, nuevoModuloId);
            psEmp.setString(2, empresaNit);
            psEmp.setString(3, cedula);
            psEmp.executeUpdate();

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al cambiar módulo del empleado: " + e.getMessage());
            rollback(conexion);
            return false;
        } finally {
            resetAutoCommit(conexion);
            ConexionBD.cerrarConexion(conexion);
        }
    }

    /**
     * Desactiva un empleado (Activo = 0).
     * También cierra su registro activo en HISTORIAL_MODULO_EMPLEADO.
     * Los registros históricos permanecen intactos para informes futuros.
     * NUNCA ejecuta DELETE sobre EMPLEADO_OPERATIVO.
     */
    public boolean desactivar(String cedula) {
        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            String hoy = LocalDate.now().toString();

            // marcar como inactivo
            PreparedStatement psEmp = conexion.prepareStatement(
                    "UPDATE EMPLEADO_OPERATIVO SET Activo = 0 WHERE Numero_cedula = ?");
            psEmp.setString(1, cedula);
            psEmp.executeUpdate();

            // cerrar el historial activo
            PreparedStatement psHist = conexion.prepareStatement(
                    "UPDATE HISTORIAL_MODULO_EMPLEADO SET FechaFin = ? " +
                    "WHERE Numero_cedula = ? AND FechaFin IS NULL");
            psHist.setString(1, hoy);
            psHist.setString(2, cedula);
            psHist.executeUpdate();

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al desactivar empleado: " + e.getMessage());
            rollback(conexion);
            return false;
        } finally {
            resetAutoCommit(conexion);
            ConexionBD.cerrarConexion(conexion);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers privados
    // -----------------------------------------------------------------------

    private EmpleadoOperativo construirDesdeResultSet(ResultSet rs) throws SQLException {
        EmpleadoOperativo e = new EmpleadoOperativo();
        e.setNumeroCedula(rs.getString("Numero_cedula"));
        e.setNombre(rs.getString("Nombre"));
        e.setTelefono(rs.getString("Telefono"));
        e.setEdad(rs.getInt("Edad"));
        e.setSexo(rs.getString("Sexo"));
        e.setCargoRol(rs.getString("Cargo_rol"));
        e.setCorreo(rs.getString("Correo"));
        e.setHashContrasena(rs.getString("HashContrasena"));
        e.setActivo(rs.getInt("Activo"));
        e.setModuloOperativoId(rs.getString("MODULO_OPERATIVO_idMODULO_OPERATIVO"));
        e.setModuloOperativoEmpresaNit(rs.getString("MODULO_OPERATIVO_EMPRESA_NIT"));
        return e;
    }

    private void setNullable(PreparedStatement ps, int index, String value) throws SQLException {
        if (value != null && !value.trim().isEmpty()) {
            ps.setString(index, value.trim());
        } else {
            ps.setNull(index, Types.VARCHAR);
        }
    }

    private void rollback(Connection conexion) {
        if (conexion != null) {
            try { conexion.rollback(); } catch (SQLException ex) { /* ignorar */ }
        }
    }

    private void resetAutoCommit(Connection conexion) {
        if (conexion != null) {
            try { conexion.setAutoCommit(true); } catch (SQLException ex) { /* ignorar */ }
        }
    }
}
