package co.optimak.servlet;

import co.optimak.dao.ModuloOperativoDAO;
import co.optimak.dao.MonitoreoDAO;
import co.optimak.modelo.ModuloOperativo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet del módulo Monitoreo en Tiempo Real.
 * Solo maneja GET — los datos son de solo lectura.
 *
 * GET /monitoreo          → vista completa
 * GET /monitoreo?async=true → fragmento HTML de tarjetas (para polling JS)
 *
 * Registrado solo en web.xml — sin @WebServlet.
 */
public class MonitoreoServlet extends HttpServlet {

    private MonitoreoDAO monitoreoDAO;
    private ModuloOperativoDAO moduloDAO;

    @Override
    public void init() {
        monitoreoDAO = new MonitoreoDAO();
        moduloDAO    = new ModuloOperativoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String empresaNit   = (String) session.getAttribute("sesionEmpresaNit");
        String sesionNombre = (String) session.getAttribute("sesionNombre");

        // Datos del turno actual
        List<Object[]> estadoModulos  = monitoreoDAO.estadoModulosHoy(empresaNit);
        List<Object[]> incidencias    = monitoreoDAO.incidenciasHoy(empresaNit);

        // Detalle por módulo: mapa moduloId → lista de operarios
        List<ModuloOperativo> listaModulos = moduloDAO.listarConConteoOperarios(empresaNit);
        Map<String, List<Object[]>> detallesPorModulo = new LinkedHashMap<>();
        for (ModuloOperativo mod : listaModulos) {
            detallesPorModulo.put(
                mod.getIdModulo(),
                monitoreoDAO.detalleOperariosModuloHoy(mod.getIdModulo(), empresaNit)
            );
        }

        request.setAttribute("estadoModulos",      estadoModulos);
        request.setAttribute("incidenciasHoy",     incidencias);
        request.setAttribute("detallesPorModulo",  detallesPorModulo);
        request.setAttribute("sesionNombre",        sesionNombre);

        // Si el parámetro async=true, devuelve solo el fragmento de tarjetas (para el polling)
        boolean async = "true".equals(request.getParameter("async"));
        request.setAttribute("soloTarjetas", async);

        request.getRequestDispatcher("/WEB-INF/vistas/monitoreo.jsp").forward(request, response);
    }
}
