package co.optimak.servlet;

import co.optimak.dao.*;
import co.optimak.modelo.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet del módulo Informes Históricos.
 * Solo maneja GET — los informes son de solo lectura.
 *
 * GET /informes?tab=operario|modulo|lote  (+ parámetros de filtro)
 *
 * Tab operario: cedula, fechaDesde, fechaHasta
 * Tab modulo:   moduloId, fechaDesde, fechaHasta
 * Tab lote:     loteOm
 *
 * Registrado solo en web.xml — sin @WebServlet.
 */
public class InformesServlet extends HttpServlet {

    private InformesDAO informesDAO;
    private EmpleadoDAO empleadoDAO;
    private ModuloOperativoDAO moduloDAO;
    private LoteDAO loteDAO;

    @Override
    public void init() {
        informesDAO = new InformesDAO();
        empleadoDAO = new EmpleadoDAO();
        moduloDAO   = new ModuloOperativoDAO();
        loteDAO     = new LoteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String empresaNit = (String) session.getAttribute("sesionEmpresaNit");
        String sesionNombre = (String) session.getAttribute("sesionNombre");

        String tab = request.getParameter("tab");
        if (tab == null) tab = "operario";

        // Selectores siempre disponibles para los filtros de cada tab
        request.setAttribute("listaEmpleados", empleadoDAO.listarTodosActivos(empresaNit));
        request.setAttribute("listaModulos",   moduloDAO.listarConConteoOperarios(empresaNit));
        request.setAttribute("listaLotes",     loteDAO.listarTodos());
        request.setAttribute("tabActivo",      tab);
        request.setAttribute("sesionNombre",   sesionNombre);

        switch (tab) {
            case "modulo" -> cargarInformeModulo(empresaNit, request);
            case "lote"   -> cargarInformeLote(request);
            default       -> cargarInformeOperario(request);
        }

        request.getRequestDispatcher("/WEB-INF/vistas/informes.jsp").forward(request, response);
    }

    // -----------------------------------------------------------------------
    // Tab 1: Eficiencia por operario
    // -----------------------------------------------------------------------

    private void cargarInformeOperario(HttpServletRequest request) {
        String cedula     = request.getParameter("cedula");
        String fechaDesde = request.getParameter("fechaDesde");
        String fechaHasta = request.getParameter("fechaHasta");

        // devolver los filtros al JSP para que los muestre en el form
        request.setAttribute("filtroCedula",    cedula);
        request.setAttribute("filtroFechaDesde", fechaDesde);
        request.setAttribute("filtroFechaHasta", fechaHasta);

        if (cedula == null || cedula.trim().isEmpty()) return;
        if (fechaDesde == null || fechaDesde.trim().isEmpty()) fechaDesde = LocalDate.now().toString();
        if (fechaHasta == null || fechaHasta.trim().isEmpty()) fechaHasta = LocalDate.now().toString();

        cedula = cedula.trim();

        EmpleadoOperativo emp = empleadoDAO.buscarPorCedula(cedula);
        if (emp == null) {
            request.setAttribute("errorInforme", "No se encontró el operario con esa cédula.");
            return;
        }

        request.setAttribute("registrosEficiencia",
                informesDAO.registrosEficienciaOperario(cedula, fechaDesde, fechaHasta));
        request.setAttribute("resumenOperario",
                informesDAO.resumenOperario(cedula, fechaDesde, fechaHasta));
        request.setAttribute("incidenciasOperario",
                informesDAO.incidenciasOperario(cedula, fechaDesde, fechaHasta));
        request.setAttribute("nombreEmpleado", emp.getNombre());
        request.setAttribute("hayDatosOperario", true);
    }

    // -----------------------------------------------------------------------
    // Tab 2: Eficiencia por módulo
    // -----------------------------------------------------------------------

    private void cargarInformeModulo(String empresaNit, HttpServletRequest request) {
        String moduloId   = request.getParameter("moduloId");
        String fechaDesde = request.getParameter("fechaDesde");
        String fechaHasta = request.getParameter("fechaHasta");

        request.setAttribute("filtroModuloId",   moduloId);
        request.setAttribute("filtroFechaDesde",  fechaDesde);
        request.setAttribute("filtroFechaHasta",  fechaHasta);

        if (moduloId == null || moduloId.trim().isEmpty()) return;
        if (fechaDesde == null || fechaDesde.trim().isEmpty()) fechaDesde = LocalDate.now().minusDays(6).toString();
        if (fechaHasta == null || fechaHasta.trim().isEmpty()) fechaHasta = LocalDate.now().toString();

        moduloId = moduloId.trim();

        List<Object[]> eficienciaDiaria = informesDAO.eficienciaDiariaModulo(
                moduloId, empresaNit, fechaDesde, fechaHasta);
        List<Object[]> ranking = informesDAO.rankingOperariosModulo(
                moduloId, empresaNit, fechaDesde, fechaHasta);

        request.setAttribute("eficienciaDiaria", eficienciaDiaria);
        request.setAttribute("rankingOperarios",  ranking);
        request.setAttribute("hayDatosModulo",    true);
    }

    // -----------------------------------------------------------------------
    // Tab 3: Avance por lote
    // -----------------------------------------------------------------------

    private void cargarInformeLote(HttpServletRequest request) {
        String loteOm = request.getParameter("loteOm");

        request.setAttribute("filtroLoteOm", loteOm);

        if (loteOm == null || loteOm.trim().isEmpty()) return;

        loteOm = loteOm.trim();
        Lote lote = loteDAO.buscarPorOm(loteOm);
        if (lote == null) {
            request.setAttribute("errorInforme", "No se encontró el lote con esa OM.");
            return;
        }

        List<Object[]> avance = informesDAO.avancePorOperacion(
                loteOm, lote.getReferenciaIdRef(), lote.getTotalUnidades());
        List<Object[]> porOperario = informesDAO.pulsacionesPorOperarioEnLote(loteOm);

        request.setAttribute("loteSeleccionado",     lote);
        request.setAttribute("avanceOperaciones",     avance);
        request.setAttribute("pulsacionesPorOperario", porOperario);
        request.setAttribute("hayDatosLote",          true);
    }
}
