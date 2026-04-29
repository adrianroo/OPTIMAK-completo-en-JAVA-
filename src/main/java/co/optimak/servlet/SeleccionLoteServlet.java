package co.optimak.servlet;

import co.optimak.dao.LoteDAO;
import co.optimak.modelo.Lote;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class SeleccionLoteServlet extends HttpServlet {

    private final LoteDAO loteDAO = new LoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String moduloId    = (String) session.getAttribute("sesionModulo");
        String empresaNit  = (String) session.getAttribute("sesionEmpresaNit");
        String nombre      = (String) session.getAttribute("sesionNombre");

        List<Lote> lotes = loteDAO.listarPorModulo(moduloId, empresaNit);
        request.setAttribute("lotes", lotes);
        request.setAttribute("sesionModulo", moduloId);
        request.setAttribute("sesionNombre", nombre);

        request.getRequestDispatcher("/WEB-INF/vistas/seleccion-lote.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String loteOm = request.getParameter("loteOm");
        if (loteOm == null || loteOm.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seleccion-lote");
            return;
        }

        HttpSession session = request.getSession(false);
        session.setAttribute("sesionLoteOm", loteOm.trim());
        // Al cambiar de lote se limpia la operación anterior
        session.removeAttribute("sesionOperacionId");

        response.sendRedirect(request.getContextPath() + "/seleccion-operacion");
    }
}
