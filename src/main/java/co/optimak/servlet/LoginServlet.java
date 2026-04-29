package co.optimak.servlet;

import co.optimak.dao.AdminDAO;
import co.optimak.dao.EmpleadoDAO;
import co.optimak.modelo.Administrador;
import co.optimak.modelo.EmpleadoOperativo;
import co.optimak.util.HashUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private AdminDAO adminDAO;
    private EmpleadoDAO empleadoDAO;

    @Override
    public void init() {
        adminDAO   = new AdminDAO();
        empleadoDAO = new EmpleadoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Si ya hay sesión activa, redirigir al dashboard que corresponde
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sesionRol") != null) {
            String rol = (String) session.getAttribute("sesionRol");
            if ("admin".equals(rol)) {
                response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/operario-dashboard");
            }
            return;
        }

        request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String identificacion = request.getParameter("identificacion");
        String contrasena     = request.getParameter("contrasena");
        String rol            = request.getParameter("rol");

        if (estaVacio(identificacion) || estaVacio(contrasena) || estaVacio(rol)) {
            request.setAttribute("error", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
            return;
        }

        if ("admin".equals(rol)) {
            procesarLoginAdmin(request, response, identificacion.trim(), contrasena);
        } else if ("operario".equals(rol)) {
            procesarLoginOperario(request, response, identificacion.trim(), contrasena);
        } else {
            request.setAttribute("error", "Selecciona un rol válido.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
        }
    }

    private void procesarLoginAdmin(HttpServletRequest request, HttpServletResponse response,
                                    String identificacion, String contrasena)
            throws ServletException, IOException {

        try {
            int idAdmin = Integer.parseInt(identificacion);
            Administrador admin = adminDAO.buscarPorId(idAdmin);

            if (admin != null && HashUtil.verificar(contrasena, admin.getHashContrasena())) {
                HttpSession session = request.getSession(true);
                session.setAttribute("sesionRol",       "admin");
                session.setAttribute("sesionId",         String.valueOf(admin.getIdAdministrador()));
                session.setAttribute("sesionNombre",     admin.getNombre());
                session.setAttribute("sesionEmpresaNit", admin.getEmpresaNit());
                response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            } else {
                request.setAttribute("error", "Credenciales incorrectas.");
                request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "El ID del administrador debe ser un número entero.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
        }
    }

    private void procesarLoginOperario(HttpServletRequest request, HttpServletResponse response,
                                       String cedula, String contrasena)
            throws ServletException, IOException {

        EmpleadoOperativo empleado = empleadoDAO.buscarPorCedula(cedula);

        if (empleado != null && HashUtil.verificar(contrasena, empleado.getHashContrasena())) {
            HttpSession session = request.getSession(true);
            session.setAttribute("sesionRol",       "operario");
            session.setAttribute("sesionId",         empleado.getNumeroCedula());
            session.setAttribute("sesionNombre",     empleado.getNombre());
            session.setAttribute("sesionModulo",     empleado.getModuloOperativoId());
            session.setAttribute("sesionEmpresaNit", empleado.getModuloOperativoEmpresaNit());
            response.sendRedirect(request.getContextPath() + "/operario-dashboard");
        } else {
            request.setAttribute("error", "Credenciales incorrectas o usuario inactivo.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
