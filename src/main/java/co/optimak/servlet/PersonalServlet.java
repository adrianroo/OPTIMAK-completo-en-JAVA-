package co.optimak.servlet;

import co.optimak.dao.*;
import co.optimak.modelo.*;
import co.optimak.util.HashUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet del módulo Gestión de Personal.
 * Maneja tres tabs: empleados, modulos, horarios.
 *
 * GET  /personal?tab=empleados|modulos|horarios
 *      Para el tab horarios, acepta además: moduloId=X&fecha=YYYY-MM-DD
 *
 * POST /personal  (entidad=empleado|modulo|jornada, accion=...)
 *
 * Registrado SOLO en web.xml — sin @WebServlet.
 */
public class PersonalServlet extends HttpServlet {

    private EmpleadoDAO empleadoDAO;
    private ModuloOperativoDAO moduloDAO;
    private JornadaModuloDAO jornadaDAO;
    private PausaModuloDAO pausaDAO;

    @Override
    public void init() {
        empleadoDAO = new EmpleadoDAO();
        moduloDAO   = new ModuloOperativoDAO();
        jornadaDAO  = new JornadaModuloDAO();
        pausaDAO    = new PausaModuloDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tab = request.getParameter("tab");
        if (tab == null) tab = "empleados";

        HttpSession session = request.getSession(false);
        String empresaNit = (String) session.getAttribute("sesionEmpresaNit");

        cargarDatosComunes(empresaNit, request);

        if ("horarios".equals(tab)) {
            cargarDatosHorarios(empresaNit, request);
        }

        request.setAttribute("tabActivo", tab);
        request.getRequestDispatcher("/WEB-INF/vistas/personal.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String empresaNit = (String) session.getAttribute("sesionEmpresaNit");

        String accion  = request.getParameter("accion");
        String entidad = request.getParameter("entidad");
        String mensaje;
        String tab;

        if ("modulo".equals(entidad)) {
            tab     = "modulos";
            mensaje = procesarModulo(accion, empresaNit, request);
        } else if ("jornada".equals(entidad)) {
            tab     = "horarios";
            mensaje = procesarJornada(accion, empresaNit, request);
        } else {
            tab     = "empleados";
            mensaje = procesarEmpleado(accion, empresaNit, request);
        }

        cargarDatosComunes(empresaNit, request);
        if ("horarios".equals(tab)) {
            cargarDatosHorarios(empresaNit, request);
        }

        request.setAttribute("tabActivo", tab);
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/WEB-INF/vistas/personal.jsp").forward(request, response);
    }

    // -----------------------------------------------------------------------
    // Carga de datos para la vista
    // -----------------------------------------------------------------------

    private void cargarDatosComunes(String empresaNit, HttpServletRequest request) {
        request.setAttribute("listaEmpleados", empleadoDAO.listarTodosActivos(empresaNit));
        request.setAttribute("listaModulos",   moduloDAO.listarConConteoOperarios(empresaNit));
    }

    private void cargarDatosHorarios(String empresaNit, HttpServletRequest request) {
        String moduloSeleccionado = request.getParameter("moduloId");
        String fechaSeleccionada  = request.getParameter("fecha");

        request.setAttribute("moduloHorarioSeleccionado", moduloSeleccionado);
        request.setAttribute("fechaHorarioSeleccionada",  fechaSeleccionada);

        if (moduloSeleccionado != null && !moduloSeleccionado.trim().isEmpty()
                && fechaSeleccionada != null && !fechaSeleccionada.trim().isEmpty()) {

            JornadaModulo jornada = jornadaDAO.buscarPorModuloYFecha(
                    moduloSeleccionado, empresaNit, fechaSeleccionada);
            request.setAttribute("jornadaActual", jornada);

            if (jornada != null) {
                List<PausaModulo> pausas = pausaDAO.listarPorJornada(jornada.getIdJornada());
                request.setAttribute("pausasActuales", pausas);
            }

            // historial de fechas ya configuradas para ese módulo
            request.setAttribute("jornadasDelModulo",
                    jornadaDAO.listarPorModulo(moduloSeleccionado, empresaNit));
        }
    }

    // -----------------------------------------------------------------------
    // Procesadores por entidad
    // -----------------------------------------------------------------------

    private String procesarEmpleado(String accion, String empresaNit, HttpServletRequest request) {
        if ("insertar".equals(accion)) {
            EmpleadoOperativo emp = leerFormularioEmpleado(request, empresaNit);
            if (emp == null) return "Error: datos del formulario incompletos.";
            // hashear contraseña antes de insertar
            String contrasena = request.getParameter("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return "La contraseña no puede estar vacía.";
            }
            emp.setHashContrasena(HashUtil.hashear(contrasena.trim()));
            return empleadoDAO.insertar(emp)
                    ? "Operario creado correctamente."
                    : "No se pudo crear el operario. Verifique que la cédula y el correo no existan.";
        }
        if ("actualizarDatos".equals(accion)) {
            EmpleadoOperativo emp = leerFormularioEmpleado(request, empresaNit);
            if (emp == null) return "Error: datos del formulario incompletos.";
            return empleadoDAO.actualizarDatos(emp)
                    ? "Datos del operario actualizados."
                    : "No se pudieron actualizar los datos.";
        }
        if ("cambiarModulo".equals(accion)) {
            String cedula        = request.getParameter("cedula");
            String nuevoModuloId = request.getParameter("nuevoModuloId");
            if (cedula == null || nuevoModuloId == null) return "Datos incompletos.";
            return empleadoDAO.cambiarModulo(cedula.trim(), nuevoModuloId.trim(), empresaNit)
                    ? "Módulo del operario actualizado correctamente."
                    : "No se pudo cambiar el módulo. Intente de nuevo.";
        }
        if ("desactivar".equals(accion)) {
            String cedula = request.getParameter("cedula");
            if (cedula == null) return "Cédula no proporcionada.";
            return empleadoDAO.desactivar(cedula.trim())
                    ? "Operario desactivado. Sus registros históricos se conservan."
                    : "No se pudo desactivar el operario.";
        }
        return "";
    }

    private String procesarModulo(String accion, String empresaNit, HttpServletRequest request) {
        if ("insertar".equals(accion)) {
            String idModulo = request.getParameter("idModulo");
            if (idModulo == null || idModulo.trim().isEmpty()) return "El ID del módulo es obligatorio.";
            if (idModulo.trim().length() > 2) return "El ID del módulo no puede tener más de 2 caracteres.";
            return moduloDAO.insertar(idModulo.trim(), empresaNit)
                    ? "Módulo " + idModulo.trim().toUpperCase() + " creado correctamente."
                    : "No se pudo crear el módulo. Verifique que el ID no exista.";
        }
        if ("eliminar".equals(accion)) {
            String idModulo = request.getParameter("idModulo");
            if (idModulo == null) return "ID de módulo no proporcionado.";
            return moduloDAO.eliminar(idModulo.trim(), empresaNit)
                    ? "Módulo eliminado."
                    : "No se pudo eliminar. El módulo tiene empleados o lotes asignados.";
        }
        return "";
    }

    private String procesarJornada(String accion, String empresaNit, HttpServletRequest request) {
        if ("guardar".equals(accion)) {
            String moduloId = request.getParameter("moduloId");
            String fecha    = request.getParameter("fecha");
            if (moduloId == null || fecha == null || moduloId.trim().isEmpty() || fecha.trim().isEmpty()) {
                return "Módulo y fecha son obligatorios.";
            }

            JornadaModulo jornada = new JornadaModulo();
            jornada.setModuloOperativoId(moduloId.trim());
            jornada.setModuloOperativoEmpresaNit(empresaNit);
            jornada.setFecha(fecha.trim());
            jornada.setHoraInicio(request.getParameter("horaInicio"));
            jornada.setHoraFin(request.getParameter("horaFin"));
            jornada.setEsDiaLaborable("on".equals(request.getParameter("diaNoLaborable")) ? 0 : 1);

            List<PausaModulo> pausas = leerPausasDelFormulario(request);

            boolean ok = jornadaDAO.guardarJornadaConPausas(jornada, pausas);

            // conservar el módulo y la fecha para que el JSP los recargue
            request.setAttribute("moduloHorarioSeleccionado", moduloId.trim());
            request.setAttribute("fechaHorarioSeleccionada", fecha.trim());

            return ok ? "Configuración de horario guardada correctamente."
                      : "No se pudo guardar la configuración. Verifique los datos.";
        }
        if ("eliminar".equals(accion)) {
            String idJornada = request.getParameter("idJornada");
            if (idJornada == null) return "ID de jornada no proporcionado.";
            boolean ok = jornadaDAO.eliminar(Long.parseLong(idJornada));
            return ok ? "Configuración de esa fecha eliminada." : "No se pudo eliminar.";
        }
        return "";
    }

    // -----------------------------------------------------------------------
    // Lectores de formulario
    // -----------------------------------------------------------------------

    private EmpleadoOperativo leerFormularioEmpleado(HttpServletRequest request, String empresaNit) {
        String cedula = request.getParameter("cedula");
        if (cedula == null || cedula.trim().isEmpty()) return null;

        EmpleadoOperativo emp = new EmpleadoOperativo();
        emp.setNumeroCedula(cedula.trim());
        emp.setNombre(request.getParameter("nombre"));
        emp.setTelefono(request.getParameter("telefono"));
        emp.setEdad(parsearEntero(request.getParameter("edad")));
        emp.setSexo(request.getParameter("sexo"));
        emp.setCargoRol(request.getParameter("cargoRol"));
        emp.setCorreo(request.getParameter("correo"));
        emp.setModuloOperativoId(request.getParameter("moduloOperativoId"));
        emp.setModuloOperativoEmpresaNit(empresaNit);
        return emp;
    }

    private List<PausaModulo> leerPausasDelFormulario(HttpServletRequest request) {
        List<PausaModulo> pausas = new ArrayList<>();
        String[] tipos     = request.getParameterValues("pausaTipo[]");
        String[] horas     = request.getParameterValues("pausaHora[]");
        String[] duraciones = request.getParameterValues("pausaDuracion[]");

        if (tipos == null) return pausas;

        for (int i = 0; i < tipos.length; i++) {
            String tipo     = tipos[i];
            String hora     = (horas != null && i < horas.length)     ? horas[i]     : null;
            String duracion = (duraciones != null && i < duraciones.length) ? duraciones[i] : null;

            if (tipo == null || tipo.trim().isEmpty()) continue;
            if (hora == null || hora.trim().isEmpty()) continue;
            int dur = parsearEntero(duracion);
            if (dur <= 0) continue;

            PausaModulo p = new PausaModulo();
            p.setTipoPausa(tipo.trim());
            p.setHoraInicioPausa(hora.trim());
            p.setDuracionMinutos(dur);
            pausas.add(p);
        }
        return pausas;
    }

    private int parsearEntero(String valor) {
        try {
            if (valor == null || valor.trim().isEmpty()) return 0;
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
