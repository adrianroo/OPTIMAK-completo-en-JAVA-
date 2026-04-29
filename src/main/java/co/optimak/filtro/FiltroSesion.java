package co.optimak.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Intercepta TODAS las peticiones (/*) y verifica que haya sesión activa.
 * También controla que cada rol solo pueda acceder a sus propias rutas.
 */
public class FiltroSesion implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI().substring(contextPath.length());

        // Recursos siempre accesibles: login, logout, CSS, imágenes
        if (esRecursoPublico(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // Bloquear acceso directo a archivos .jsp
        // Los JSPs solo deben ser accedidos via forward desde un servlet
        if (uri.endsWith(".jsp")) {
            HttpSession s = request.getSession(false);
            if (s != null && "admin".equals(s.getAttribute("sesionRol"))) {
                response.sendRedirect(contextPath + "/admin-dashboard");
            } else if (s != null && "operario".equals(s.getAttribute("sesionRol"))) {
                response.sendRedirect(contextPath + "/operario-dashboard");
            } else {
                response.sendRedirect(contextPath + "/login");
            }
            return;
        }

        // Verificar sesión
        HttpSession session = request.getSession(false);
        boolean haySession = session != null && session.getAttribute("sesionRol") != null;

        if (!haySession) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String rol = (String) session.getAttribute("sesionRol");

        // Rutas exclusivas del administrador
        if (esRutaAdmin(uri) && !"admin".equals(rol)) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Rutas exclusivas del operario
        if (esRutaOperario(uri) && !"operario".equals(rol)) {
            response.sendRedirect(contextPath + "/admin-dashboard");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean esRecursoPublico(String uri) {
        return uri.equals("/login")
            || uri.equals("/logout")
            || uri.startsWith("/css/")
            || uri.startsWith("/img/")
            || uri.startsWith("/js/");
    }

    private boolean esRutaAdmin(String uri) {
        return uri.equals("/admin-dashboard")
            || uri.equals("/produccion")
            || uri.equals("/personal")
            || uri.equals("/informes")
            || uri.equals("/monitoreo")
            || uri.equals("/lotes"); // módulo existente, se unifica en /produccion más adelante
    }

    private boolean esRutaOperario(String uri) {
        return uri.equals("/operario-dashboard")
            || uri.equals("/seleccion-lote")
            || uri.equals("/seleccion-operacion")
            || uri.equals("/pulsacion");
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
