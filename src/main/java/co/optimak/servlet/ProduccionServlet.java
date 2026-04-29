package co.optimak.servlet;

import co.optimak.dao.LoteDAO;
import co.optimak.dao.ModuloOperativoDAO;
import co.optimak.dao.OperacionDAO;
import co.optimak.dao.ReferenciaDAO;
import co.optimak.modelo.Lote;
import co.optimak.modelo.Operacion;
import co.optimak.modelo.Referencia;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet principal del módulo Gestión de Producción.
 * Maneja tres tabs en una sola vista: lotes, referencias y operaciones.
 *
 * GET  /produccion?tab=lotes|referencias|operaciones  → carga lista del tab
 * POST /produccion  (entidad=lote|referencia|operacion, accion=insertar|actualizar|eliminar)
 *
 * Registrado SOLO en web.xml — sin @WebServlet para evitar el doble mapeo.
 */
public class ProduccionServlet extends HttpServlet {

    private LoteDAO loteDAO;
    private ReferenciaDAO referenciaDAO;
    private OperacionDAO operacionDAO;
    private ModuloOperativoDAO moduloDAO;

    @Override
    public void init() {
        loteDAO      = new LoteDAO();
        referenciaDAO = new ReferenciaDAO();
        operacionDAO  = new OperacionDAO();
        moduloDAO     = new ModuloOperativoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tab = request.getParameter("tab");
        if (tab == null) tab = "lotes";

        cargarDatosParaVista(tab, request);
        request.setAttribute("tabActivo", tab);
        request.getRequestDispatcher("/WEB-INF/vistas/produccion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion  = request.getParameter("accion");
        String entidad = request.getParameter("entidad");
        String mensaje;
        String tab;

        if ("referencia".equals(entidad)) {
            tab     = "referencias";
            mensaje = procesarReferencia(accion, request);
        } else if ("operacion".equals(entidad)) {
            tab     = "operaciones";
            mensaje = procesarOperacion(accion, request);
        } else {
            tab     = "lotes";
            mensaje = procesarLote(accion, request);
        }

        cargarDatosParaVista(tab, request);
        request.setAttribute("tabActivo", tab);
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/WEB-INF/vistas/produccion.jsp").forward(request, response);
    }

    // carga todas las listas siempre — el dataset es pequeño y simplifica el JSP
    // (los tabs cambian con JS client-side, así que todos los datos deben estar en el HTML)
    private void cargarDatosParaVista(String tab, HttpServletRequest request) {
        request.setAttribute("listaReferencias", referenciaDAO.listarTodas());
        request.setAttribute("listaModulos",     moduloDAO.listarTodos());
        request.setAttribute("listaOperaciones", operacionDAO.listarTodas());
        request.setAttribute("listaLotes",       loteDAO.listarTodos());
    }

    // -------------------------------------------------------------------------
    // Procesadores por entidad
    // -------------------------------------------------------------------------

    private String procesarReferencia(String accion, HttpServletRequest request) {
        if ("insertar".equals(accion)) {
            Referencia ref = leerFormularioReferencia(request);
            return referenciaDAO.insertar(ref)
                    ? "Referencia creada correctamente."
                    : "No se pudo crear la referencia. Verifique que el ID no exista.";
        }
        if ("actualizar".equals(accion)) {
            Referencia ref = leerFormularioReferencia(request);
            return referenciaDAO.actualizar(ref)
                    ? "Referencia actualizada correctamente."
                    : "No se pudo actualizar la referencia.";
        }
        if ("eliminar".equals(accion)) {
            String idRef = request.getParameter("idRef");
            return referenciaDAO.eliminar(idRef)
                    ? "Referencia eliminada."
                    : "No se pudo eliminar. Verifique que no tenga lotes u operaciones asociadas.";
        }
        return "";
    }

    private String procesarOperacion(String accion, HttpServletRequest request) {
        if ("insertar".equals(accion)) {
            Operacion op = leerFormularioOperacion(request);
            return operacionDAO.insertar(op)
                    ? "Operación creada correctamente."
                    : "No se pudo crear la operación.";
        }
        if ("actualizar".equals(accion)) {
            Operacion op = leerFormularioOperacion(request);
            return operacionDAO.actualizar(op)
                    ? "Operación actualizada correctamente."
                    : "No se pudo actualizar la operación.";
        }
        if ("eliminar".equals(accion)) {
            int id = Integer.parseInt(request.getParameter("idOperacion"));
            return operacionDAO.eliminar(id)
                    ? "Operación eliminada."
                    : "No se pudo eliminar. Puede tener registros de producción asociados.";
        }
        return "";
    }

    private String procesarLote(String accion, HttpServletRequest request) {
        if ("insertar".equals(accion)) {
            Lote lote = leerFormularioLote(request);
            return loteDAO.insertar(lote)
                    ? "Lote creado correctamente."
                    : "No se pudo crear el lote. Verifique que la OM no exista.";
        }
        if ("actualizar".equals(accion)) {
            Lote lote = leerFormularioLote(request);
            return loteDAO.actualizar(lote)
                    ? "Lote actualizado correctamente."
                    : "No se pudo actualizar el lote.";
        }
        if ("eliminar".equals(accion)) {
            String om = request.getParameter("om");
            return loteDAO.eliminar(om)
                    ? "Lote eliminado."
                    : "No se pudo eliminar el lote.";
        }
        return "";
    }

    // -------------------------------------------------------------------------
    // Lectores de formulario
    // -------------------------------------------------------------------------

    private Referencia leerFormularioReferencia(HttpServletRequest request) {
        Referencia ref = new Referencia();
        ref.setIdRef(request.getParameter("idRef"));
        ref.setTipoPrenda(request.getParameter("tipoPrenda"));
        ref.setCliente(request.getParameter("cliente"));
        ref.setColeccion(request.getParameter("coleccion"));
        ref.setPrecioUnitario(parsearDecimal(request.getParameter("precioUnitario")));
        return ref;
    }

    private Operacion leerFormularioOperacion(HttpServletRequest request) {
        Operacion op = new Operacion();
        String idStr = request.getParameter("idOperacion");
        if (idStr != null && !idStr.trim().isEmpty()) {
            op.setIdOperacion(Integer.parseInt(idStr.trim()));
        }
        op.setNombreCorto(request.getParameter("nombreCorto"));
        op.setDetalle(request.getParameter("detalle"));
        op.setMaquina(request.getParameter("maquina"));
        op.setSamOperacion(parsearDecimal(request.getParameter("samOperacion")));
        op.setReferenciaIdRef(request.getParameter("referenciaIdRef"));
        return op;
    }

    private Lote leerFormularioLote(HttpServletRequest request) {
        Lote lote = new Lote();
        lote.setOm(request.getParameter("om"));
        lote.setNumeroRemisionEntrada(request.getParameter("numeroRemisionEntrada"));
        lote.setOc(request.getParameter("oc"));
        lote.setColor(request.getParameter("color"));
        lote.setFechaIngresoPlanita(request.getParameter("fechaIngreso"));
        lote.setReferenciaIdRef(request.getParameter("referenciaIdRef"));
        lote.setModuloOperativoId(request.getParameter("moduloOperativoId"));
        // el NIT viene de la sesión, no del formulario
        HttpSession session = request.getSession(false);
        lote.setModuloOperativoEmpresaNit((String) session.getAttribute("sesionEmpresaNit"));
        lote.setCantXXL(parsearEntero(request.getParameter("cantXXL")));
        lote.setCantXL(parsearEntero(request.getParameter("cantXL")));
        lote.setCantL(parsearEntero(request.getParameter("cantL")));
        lote.setCantM(parsearEntero(request.getParameter("cantM")));
        lote.setCantS(parsearEntero(request.getParameter("cantS")));
        lote.setCantXS(parsearEntero(request.getParameter("cantXS")));
        return lote;
    }

    // -------------------------------------------------------------------------
    // Utilidades de parseo
    // -------------------------------------------------------------------------

    private int parsearEntero(String valor) {
        try {
            if (valor == null || valor.trim().isEmpty()) return 0;
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parsearDecimal(String valor) {
        try {
            if (valor == null || valor.trim().isEmpty()) return 0.0;
            return Double.parseDouble(valor.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
