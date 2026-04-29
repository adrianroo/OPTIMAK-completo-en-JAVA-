package co.optimak.servlet;

import co.optimak.dao.LoteDAO;
import co.optimak.dao.OperacionDAO;
import co.optimak.modelo.Lote;
import co.optimak.modelo.Operacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class SeleccionOperacionServlet extends HttpServlet {

    private final LoteDAO loteDAO         = new LoteDAO();
    private final OperacionDAO operacionDAO = new OperacionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String loteOm = (String) session.getAttribute("sesionLoteOm");

        if (loteOm == null) {
            response.sendRedirect(request.getContextPath() + "/seleccion-lote");
            return;
        }

        Lote lote = loteDAO.buscarPorOm(loteOm);
        if (lote == null) {
            session.removeAttribute("sesionLoteOm");
            response.sendRedirect(request.getContextPath() + "/seleccion-lote");
            return;
        }

        List<Operacion> operaciones = operacionDAO.listarPorReferencia(lote.getReferenciaIdRef());

        request.setAttribute("lote", lote);
        request.setAttribute("operaciones", operaciones);
        request.setAttribute("sesionModulo", session.getAttribute("sesionModulo"));
        request.setAttribute("sesionNombre", session.getAttribute("sesionNombre"));

        request.getRequestDispatcher("/WEB-INF/vistas/seleccion-operacion.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String operacionIdStr = request.getParameter("operacionId");
        if (operacionIdStr == null || operacionIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seleccion-operacion");
            return;
        }

        try {
            int operacionId = Integer.parseInt(operacionIdStr.trim());
            HttpSession session = request.getSession(false);
            session.setAttribute("sesionOperacionId", operacionId);
            response.sendRedirect(request.getContextPath() + "/pulsacion");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/seleccion-operacion");
        }
    }
}
