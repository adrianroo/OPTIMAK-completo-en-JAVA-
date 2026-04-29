package co.optimak.servlet;

import co.optimak.dao.EmpresaDAO;
import co.optimak.modelo.Empresa;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class OperarioDashboardServlet extends HttpServlet {

    private EmpresaDAO empresaDAO;

    @Override
    public void init() {
        empresaDAO = new EmpresaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String empresaNit = (String) session.getAttribute("sesionEmpresaNit");

        Empresa empresa = empresaDAO.buscarPorNit(empresaNit);
        request.setAttribute("empresa", empresa);

        request.getRequestDispatcher("/WEB-INF/vistas/operario-dashboard.jsp").forward(request, response);
    }
}
