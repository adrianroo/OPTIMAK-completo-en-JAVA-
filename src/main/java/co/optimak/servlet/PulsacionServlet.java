package co.optimak.servlet;

import co.optimak.dao.*;
import co.optimak.modelo.*;
import co.optimak.util.CalculadorEficiencia;
import co.optimak.util.SistemaAlertas;
import co.optimak.util.ValidadorProduccion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PulsacionServlet extends HttpServlet {

    private final LoteDAO               loteDAO        = new LoteDAO();
    private final OperacionDAO          operacionDAO   = new OperacionDAO();
    private final RegistroPulsacionDAO  pulsacionDAO   = new RegistroPulsacionDAO();
    private final RegistroEficienciaDAO eficienciaDAO  = new RegistroEficienciaDAO();
    private final IncidenciaDAO         incidenciaDAO  = new IncidenciaDAO();
    private final JornadaModuloDAO      jornadaDAO     = new JornadaModuloDAO();
    private final PausaModuloDAO        pausaDAO       = new PausaModuloDAO();

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String cedula      = (String) session.getAttribute("sesionId");
        String moduloId    = (String) session.getAttribute("sesionModulo");
        String empresaNit  = (String) session.getAttribute("sesionEmpresaNit");
        String loteOm      = (String) session.getAttribute("sesionLoteOm");
        Object opIdObj     = session.getAttribute("sesionOperacionId");

        if (loteOm == null) {
            response.sendRedirect(request.getContextPath() + "/seleccion-lote");
            return;
        }
        if (opIdObj == null) {
            response.sendRedirect(request.getContextPath() + "/seleccion-operacion");
            return;
        }

        int operacionId = (Integer) opIdObj;
        String fecha = LocalDate.now().toString(); // "yyyy-MM-dd"

        Lote lote           = loteDAO.buscarPorOm(loteOm);
        Operacion operacion = operacionDAO.buscarPorId(operacionId);

        if (lote == null || operacion == null) {
            session.removeAttribute("sesionLoteOm");
            session.removeAttribute("sesionOperacionId");
            response.sendRedirect(request.getContextPath() + "/seleccion-lote");
            return;
        }

        // --- Jornada y pausas ---
        JornadaModulo jornada = jornadaDAO.buscarPorModuloYFecha(moduloId, empresaNit, fecha);
        List<PausaModulo> pausas = new ArrayList<>();
        if (jornada != null) {
            pausas = pausaDAO.listarPorJornada(jornada.getIdJornada());
        }

        // --- Eficiencia individual ---
        int minutosProductivosHoy = CalculadorEficiencia.calcularMinutosProductivosHasta(
                jornada, pausas, LocalTime.now());
        double samTotalHoy = pulsacionDAO.calcularSamTotalHoy(cedula, fecha);
        double eficienciaIndividual = CalculadorEficiencia.calcularEficiencia(samTotalHoy, minutosProductivosHoy);

        // --- Eficiencia modular ---
        Map<String, Double> samPorEmpleado = pulsacionDAO.obtenerSamPorEmpleadoModuloHoy(
                moduloId, empresaNit, fecha);
        List<Double> eficienciasIndividuales = new ArrayList<>();
        for (double sam : samPorEmpleado.values()) {
            eficienciasIndividuales.add(CalculadorEficiencia.calcularEficiencia(sam, minutosProductivosHoy));
        }
        double eficienciaModular = CalculadorEficiencia.calcularEficienciaModular(eficienciasIndividuales);

        // --- Contador de pulsaciones hoy (para esta operación en este lote) ---
        int pulsacionesHoy = pulsacionDAO.contarPorEmpleadoHoy(cedula, fecha);

        // --- Registros de eficiencia para resumen del día ---
        List<RegistroEficiencia> registrosHoy = eficienciaDAO.listarPorEmpleadoYFecha(cedula, fecha);

        // --- Leer y limpiar alertas de sesión (patrón PRG) ---
        String alertaEficiencia = (String) session.getAttribute("alertaEficiencia");
        Long alertaRegId        = (Long)   session.getAttribute("alertaEficienciaRegId");
        String alertaCuello     = (String) session.getAttribute("alertaCuelloBotella");
        String errorPulsacion   = (String) session.getAttribute("errorPulsacion");
        session.removeAttribute("alertaEficiencia");
        session.removeAttribute("alertaEficienciaRegId");
        session.removeAttribute("alertaCuelloBotella");
        session.removeAttribute("errorPulsacion");

        // --- Pasar datos a la vista ---
        request.setAttribute("lote",               lote);
        request.setAttribute("operacion",          operacion);
        request.setAttribute("eficienciaIndividual", String.format("%.1f", eficienciaIndividual));
        request.setAttribute("eficienciaModular",  String.format("%.1f", eficienciaModular));
        request.setAttribute("pulsacionesHoy",     pulsacionesHoy);
        request.setAttribute("registrosHoy",       registrosHoy);
        request.setAttribute("alertaEficiencia",   alertaEficiencia);
        request.setAttribute("alertaEficienciaRegId", alertaRegId);
        request.setAttribute("alertaCuelloBotella", alertaCuello);
        request.setAttribute("errorPulsacion",     errorPulsacion);
        request.setAttribute("sesionModulo",       moduloId);
        request.setAttribute("sesionNombre",       session.getAttribute("sesionNombre"));

        request.getRequestDispatcher("/WEB-INF/vistas/pulsacion.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession(false);

        if ("incidencia".equals(accion)) {
            manejarIncidencia(request, session);
        } else {
            manejarPulsacion(request, session);
        }

        response.sendRedirect(request.getContextPath() + "/pulsacion");
    }

    // ---------- POST: registrar pulsación ----------

    private void manejarPulsacion(HttpServletRequest request, HttpSession session) {
        String cedula     = (String) session.getAttribute("sesionId");
        String moduloId   = (String) session.getAttribute("sesionModulo");
        String empresaNit = (String) session.getAttribute("sesionEmpresaNit");
        String loteOm     = (String) session.getAttribute("sesionLoteOm");
        int operacionId   = (Integer) session.getAttribute("sesionOperacionId");
        String fecha      = LocalDate.now().toString();

        Lote lote = loteDAO.buscarPorOm(loteOm);
        if (lote == null) return;

        // 1. Validar conciliación de lote
        ValidadorProduccion validador = new ValidadorProduccion(pulsacionDAO);
        if (!validador.esPulsacionValida(operacionId, lote)) {
            session.setAttribute("errorPulsacion",
                    "Este lote ya alcanzó el máximo de unidades para esta operación.");
            return;
        }

        // 2. Insertar pulsación
        RegistroPulsacion p = new RegistroPulsacion();
        p.setNumeroCedula(cedula);
        p.setOperacionId(operacionId);
        p.setLoteOm(loteOm);
        if (!pulsacionDAO.insertar(p)) {
            session.setAttribute("errorPulsacion", "Error al registrar la pulsación. Intenta de nuevo.");
            return;
        }

        // 3. Verificar si han pasado 20 minutos productivos → guardar REGISTRO_EFICIENCIA
        JornadaModulo jornada = jornadaDAO.buscarPorModuloYFecha(moduloId, empresaNit, fecha);
        if (jornada == null || jornada.getEsDiaLaborable() == 0) return;

        List<PausaModulo> pausas = pausaDAO.listarPorJornada(jornada.getIdJornada());

        RegistroEficiencia ultimoRegistro = eficienciaDAO.buscarUltimoDelEmpleadoHoy(cedula);

        LocalTime desde;
        String desdeStr;
        if (ultimoRegistro != null) {
            String periodoFin = ultimoRegistro.getPeriodoFin(); // "yyyy-MM-dd HH:mm:ss"
            desde    = LocalTime.parse(periodoFin.substring(11)); // "HH:mm:ss"
            desdeStr = periodoFin;
        } else {
            desde    = LocalTime.parse(jornada.getHoraInicio()); // "HH:mm:ss" de MySQL
            desdeStr = fecha + " " + jornada.getHoraInicio();
        }

        if (CalculadorEficiencia.hanPasadoMinutosProductivos(jornada, pausas, desde)) {
            String ahoraStr = LocalDateTime.now().format(FMT_DT);

            double[] samYCount = pulsacionDAO.calcularSamYCountEnPeriodo(cedula, desdeStr, ahoraStr);
            int minutosPeriodo  = CalculadorEficiencia.calcularMinutosProductivosEntrePeriodo(
                    jornada, pausas, desde, LocalTime.now());
            double eficiencia = CalculadorEficiencia.calcularEficiencia(samYCount[1], minutosPeriodo);

            RegistroEficiencia reg = new RegistroEficiencia();
            reg.setNumeroCedula(cedula);
            reg.setOm(loteOm);
            reg.setPeriodoInicio(desdeStr);
            reg.setPeriodoFin(ahoraStr);
            reg.setCantidadProducida((int) samYCount[0]);
            reg.setEficienciaPorcentaje(eficiencia);
            long idReg = eficienciaDAO.insertar(reg);

            // 4. Alerta de eficiencia baja
            SistemaAlertas alertas = new SistemaAlertas(pulsacionDAO);
            if (alertas.esEficienciaBaja(eficiencia) && idReg > 0) {
                session.setAttribute("alertaEficiencia", String.format("%.1f", eficiencia));
                session.setAttribute("alertaEficienciaRegId", idReg);
            }
        }

        // 5. Alerta de cuello de botella (siempre se verifica, no solo cada 30 min)
        SistemaAlertas alertas = new SistemaAlertas(pulsacionDAO);
        String cuello = alertas.detectarCuelloBotella(lote.getReferenciaIdRef(), loteOm);
        if (cuello != null) {
            session.setAttribute("alertaCuelloBotella", cuello);
        }
    }

    // ---------- POST: enviar justificación (incidencia) ----------

    private void manejarIncidencia(HttpServletRequest request, HttpSession session) {
        String descripcion = request.getParameter("descripcion");
        if (descripcion == null || descripcion.trim().isEmpty()) return;

        Long idReg = (Long) session.getAttribute("alertaEficienciaRegId");
        if (idReg == null || idReg < 1) return;

        String cedula = (String) session.getAttribute("sesionId");

        Incidencia inc = new Incidencia();
        inc.setDescripcion(descripcion.trim());
        inc.setNumeroCedula(cedula);
        inc.setIdRegistroEficiencia(idReg);
        incidenciaDAO.insertar(inc);

        // Limpiar la alerta ya que el operario respondió
        session.removeAttribute("alertaEficiencia");
        session.removeAttribute("alertaEficienciaRegId");
    }
}
