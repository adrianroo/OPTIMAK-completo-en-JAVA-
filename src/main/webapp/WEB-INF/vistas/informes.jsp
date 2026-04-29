<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="co.optimak.modelo.EmpleadoOperativo" %>
<%@ page import="co.optimak.modelo.ModuloOperativo" %>
<%@ page import="co.optimak.modelo.Lote" %>
<%@ page import="co.optimak.modelo.RegistroEficiencia" %>
<%
    String tabActivo      = (String)  request.getAttribute("tabActivo");
    if (tabActivo == null) tabActivo = "operario";
    String sesionNombre   = (String)  session.getAttribute("sesionNombre");
    String errorInforme   = (String)  request.getAttribute("errorInforme");

    String filtroCedula     = (String) request.getAttribute("filtroCedula");
    String filtroFechaDesde = (String) request.getAttribute("filtroFechaDesde");
    String filtroFechaHasta = (String) request.getAttribute("filtroFechaHasta");
    String filtroModuloId   = (String) request.getAttribute("filtroModuloId");
    String filtroLoteOm     = (String) request.getAttribute("filtroLoteOm");

    List<EmpleadoOperativo> listaEmpleados = (List<EmpleadoOperativo>) request.getAttribute("listaEmpleados");
    List<ModuloOperativo>   listaModulos   = (List<ModuloOperativo>)   request.getAttribute("listaModulos");
    List<Lote>              listaLotes     = (List<Lote>)              request.getAttribute("listaLotes");

    Boolean hayDatosOperario  = (Boolean)         request.getAttribute("hayDatosOperario");
    String  nombreEmpleado    = (String)           request.getAttribute("nombreEmpleado");
    List<RegistroEficiencia> registrosEficiencia  = (List<RegistroEficiencia>) request.getAttribute("registrosEficiencia");
    double[] resumenOperario  = (double[])         request.getAttribute("resumenOperario");
    List<Object[]> incidenciasOperario            = (List<Object[]>) request.getAttribute("incidenciasOperario");

    Boolean hayDatosModulo    = (Boolean)      request.getAttribute("hayDatosModulo");
    List<Object[]> eficienciaDiaria  = (List<Object[]>) request.getAttribute("eficienciaDiaria");
    List<Object[]> rankingOperarios  = (List<Object[]>) request.getAttribute("rankingOperarios");

    Boolean hayDatosLote      = (Boolean) request.getAttribute("hayDatosLote");
    Lote    loteSeleccionado  = (Lote)    request.getAttribute("loteSeleccionado");
    List<Object[]> avanceOperaciones      = (List<Object[]>) request.getAttribute("avanceOperaciones");
    List<Object[]> pulsacionesPorOperario = (List<Object[]>) request.getAttribute("pulsacionesPorOperario");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Informes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>

    <header class="header-principal">
        <div class="header-logo-container">
            <img src="img/logoreducido.png" alt="OPTIMAK" class="header-logo-img">
            <h1 class="header-titulo">OPTIMAK</h1>
        </div>
        <nav class="header-nav">
            <a href="admin-dashboard">Inicio</a>
            <a href="produccion">Producción</a>
            <a href="personal">Personal</a>
            <a href="informes" class="active">Informes</a>
            <a href="monitoreo">Monitoreo</a>
        </nav>
        <div class="header-usuario">
            <i class="fas fa-user-circle"></i>
            <span class="header-usuario-texto">Admin - <%= sesionNombre != null ? sesionNombre : "" %></span>
            <a href="logout" class="btn-icono" title="Cerrar sesión"><i class="fas fa-sign-out-alt"></i></a>
        </div>
    </header>

    <main class="contenedor-principal">
        <div class="tarjeta">
            <h2 class="titulo-seccion">Informes Históricos</h2>
            <p class="subtitulo-seccion">Consulta de eficiencia y producción por período</p>

            <% if (errorInforme != null && !errorInforme.isEmpty()) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i><%= errorInforme %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <div class="tabs-contenedor">
                <button class="tab <%= "operario".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('operario', this)">
                    <i class="fas fa-user me-1"></i>Por Operario
                </button>
                <button class="tab <%= "modulo".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('modulo', this)">
                    <i class="fas fa-layer-group me-1"></i>Por Módulo
                </button>
                <button class="tab <%= "lote".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('lote', this)">
                    <i class="fas fa-box me-1"></i>Por Lote
                </button>
            </div>

            <%-- ================================================================
                 TAB 1 — POR OPERARIO
            ================================================================ --%>
            <div id="tab-operario" class="tab-content"
                 style="<%= "operario".equals(tabActivo) ? "display:block" : "display:none" %>">

                <form method="get" action="informes" class="mb-4">
                    <input type="hidden" name="tab" value="operario">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label class="form-label">Operario</label>
                            <select name="cedula" class="form-select form-input">
                                <option value="">-- Seleccione un operario --</option>
                                <% if (listaEmpleados != null) {
                                    for (EmpleadoOperativo emp : listaEmpleados) { %>
                                        <option value="<%= emp.getNumeroCedula() %>"
                                            <%= emp.getNumeroCedula().equals(filtroCedula != null ? filtroCedula : "") ? "selected" : "" %>>
                                            <%= emp.getNombre() %> — Mód. <%= emp.getModuloOperativoId() %>
                                        </option>
                                <% }} %>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Desde</label>
                            <input type="date" name="fechaDesde" class="form-control form-input"
                                   value="<%= filtroFechaDesde != null ? filtroFechaDesde : "" %>">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Hasta</label>
                            <input type="date" name="fechaHasta" class="form-control form-input"
                                   value="<%= filtroFechaHasta != null ? filtroFechaHasta : "" %>">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn-principal w-100">
                                <i class="fas fa-search me-1"></i>Consultar
                            </button>
                        </div>
                    </div>
                </form>

                <% if (Boolean.TRUE.equals(hayDatosOperario)) { %>

                    <h5 class="mb-3">
                        <i class="fas fa-user-circle me-2 text-muted"></i>
                        <%= nombreEmpleado %> — Cédula: <%= filtroCedula %>
                        <span class="badge-estado badge-proceso ms-2" style="font-size:0.8rem;">
                            <%= filtroFechaDesde %> → <%= filtroFechaHasta %>
                        </span>
                    </h5>

                    <% if (resumenOperario != null) { %>
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color: <%= resumenOperario[0] >= 80 ? "#10b981" : resumenOperario[0] >= 65 ? "#f59e0b" : "#dc2626" %>">
                                    <%= String.format("%.1f", resumenOperario[0]) %>%
                                </div>
                                <div class="informe-card-label">Eficiencia promedio</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#2563eb;">
                                    <%= (int) resumenOperario[1] %>
                                </div>
                                <div class="informe-card-label">Total pulsaciones</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#f59e0b;">
                                    <%= (int) resumenOperario[2] %>
                                </div>
                                <div class="informe-card-label">Incidencias registradas</div>
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <h6 class="mb-2"><i class="fas fa-chart-line me-1"></i>Registros de eficiencia</h6>
                    <% if (registrosEficiencia != null && !registrosEficiencia.isEmpty()) { %>
                    <div class="tabla-contenedor mb-4">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Período inicio</th>
                                    <th>Período fin</th>
                                    <th>Pulsaciones</th>
                                    <th>Eficiencia</th>
                                    <th>Lote (OM)</th>
                                    <th>Observaciones admin</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (RegistroEficiencia r : registrosEficiencia) {
                                    double ef = r.getEficienciaPorcentaje();
                                    String cls = ef >= 80 ? "badge-terminado" : ef >= 65 ? "badge-proceso" : "badge-pendiente";
                                %>
                                <tr>
                                    <td><%= r.getPeriodoInicio() != null ? r.getPeriodoInicio().substring(0,16) : "" %></td>
                                    <td><%= r.getPeriodoFin()    != null ? r.getPeriodoFin().substring(0,16)    : "" %></td>
                                    <td class="text-center"><%= r.getCantidadProducida() %></td>
                                    <td class="text-center">
                                        <span class="badge-estado <%= cls %>">
                                            <%= String.format("%.1f", ef) %>%
                                        </span>
                                    </td>
                                    <td><%= r.getOm() %></td>
                                    <td class="text-muted" style="font-size:0.85rem;">
                                        <%= r.getObservaciones() != null ? r.getObservaciones() : "—" %>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                        <p class="text-muted mb-4">Sin registros de eficiencia en este período.</p>
                    <% } %>

                    <% if (incidenciasOperario != null && !incidenciasOperario.isEmpty()) { %>
                    <h6 class="mb-2">
                        <i class="fas fa-exclamation-circle me-1" style="color:#f59e0b;"></i>
                        Incidencias del período
                    </h6>
                    <div class="tabla-contenedor">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Fecha y hora</th>
                                    <th>Justificación del operario</th>
                                    <th>Eficiencia del período</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Object[] fila : incidenciasOperario) {
                                    double ef = (Double) fila[2];
                                    String cls = ef >= 80 ? "badge-terminado" : ef >= 65 ? "badge-proceso" : "badge-pendiente";
                                %>
                                <tr>
                                    <td><%= fila[0] != null ? fila[0].toString().substring(0,16) : "" %></td>
                                    <td><%= fila[1] %></td>
                                    <td class="text-center">
                                        <span class="badge-estado <%= cls %>">
                                            <%= String.format("%.1f", ef) %>%
                                        </span>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } %>

                <% } else if (filtroCedula != null && !filtroCedula.trim().isEmpty()) { %>
                    <p class="text-muted">Sin datos para los filtros seleccionados.</p>
                <% } %>

            </div><%-- fin tab operario --%>

            <%-- ================================================================
                 TAB 2 — POR MÓDULO
            ================================================================ --%>
            <div id="tab-modulo" class="tab-content"
                 style="<%= "modulo".equals(tabActivo) ? "display:block" : "display:none" %>">

                <form method="get" action="informes" class="mb-4">
                    <input type="hidden" name="tab" value="modulo">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label class="form-label">Módulo operativo</label>
                            <select name="moduloId" class="form-select form-input">
                                <option value="">-- Seleccione un módulo --</option>
                                <% if (listaModulos != null) {
                                    for (ModuloOperativo mod : listaModulos) { %>
                                        <option value="<%= mod.getIdModulo() %>"
                                            <%= mod.getIdModulo().equals(filtroModuloId != null ? filtroModuloId : "") ? "selected" : "" %>>
                                            Módulo <%= mod.getIdModulo() %> (<%= mod.getCantOperarios() %> operarios)
                                        </option>
                                <% }} %>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Desde</label>
                            <input type="date" name="fechaDesde" class="form-control form-input"
                                   value="<%= filtroFechaDesde != null ? filtroFechaDesde : "" %>">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Hasta</label>
                            <input type="date" name="fechaHasta" class="form-control form-input"
                                   value="<%= filtroFechaHasta != null ? filtroFechaHasta : "" %>">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn-principal w-100">
                                <i class="fas fa-search me-1"></i>Consultar
                            </button>
                        </div>
                    </div>
                </form>

                <% if (Boolean.TRUE.equals(hayDatosModulo)) { %>

                    <h6 class="mb-2"><i class="fas fa-calendar-alt me-1"></i>Eficiencia diaria del módulo</h6>
                    <% if (eficienciaDiaria != null && !eficienciaDiaria.isEmpty()) { %>
                    <div class="tabla-contenedor mb-4">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Fecha</th>
                                    <th>Eficiencia promedio</th>
                                    <th>Operarios activos ese día</th>
                                    <th>Nivel</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Object[] fila : eficienciaDiaria) {
                                    double ef = (Double) fila[1];
                                    String cls = ef >= 80 ? "badge-terminado" : ef >= 65 ? "badge-proceso" : "badge-pendiente";
                                    String niv = ef >= 80 ? "Buena" : ef >= 65 ? "Regular" : "Baja";
                                %>
                                <tr>
                                    <td><%= fila[0] %></td>
                                    <td class="text-center"><strong><%= String.format("%.1f", ef) %>%</strong></td>
                                    <td class="text-center"><%= fila[2] %></td>
                                    <td class="text-center">
                                        <span class="badge-estado <%= cls %>"><%= niv %></span>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                        <p class="text-muted mb-4">Sin registros de eficiencia para este módulo en el período.</p>
                    <% } %>

                    <% if (rankingOperarios != null && !rankingOperarios.isEmpty()) { %>
                    <h6 class="mb-2">
                        <i class="fas fa-trophy me-1" style="color:#f59e0b;"></i>
                        Rendimiento por operario en el período
                    </h6>
                    <div class="tabla-contenedor">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Operario</th>
                                    <th>Cédula</th>
                                    <th>Eficiencia promedio</th>
                                    <th>Períodos</th>
                                    <th>Total prendas</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% int pos = 1; for (Object[] fila : rankingOperarios) {
                                    double ef = (Double) fila[2];
                                    String cls = ef >= 80 ? "badge-terminado" : ef >= 65 ? "badge-proceso" : "badge-pendiente";
                                %>
                                <tr>
                                    <td class="text-center text-muted"><%= pos++ %></td>
                                    <td><%= fila[0] %></td>
                                    <td class="text-muted"><%= fila[1] %></td>
                                    <td class="text-center">
                                        <span class="badge-estado <%= cls %>">
                                            <%= String.format("%.1f", ef) %>%
                                        </span>
                                    </td>
                                    <td class="text-center"><%= fila[3] %></td>
                                    <td class="text-center"><%= fila[4] %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } %>

                <% } else if (filtroModuloId != null && !filtroModuloId.trim().isEmpty()) { %>
                    <p class="text-muted">Sin datos para los filtros seleccionados.</p>
                <% } %>

            </div><%-- fin tab modulo --%>

            <%-- ================================================================
                 TAB 3 — POR LOTE
            ================================================================ --%>
            <div id="tab-lote" class="tab-content"
                 style="<%= "lote".equals(tabActivo) ? "display:block" : "display:none" %>">

                <form method="get" action="informes" class="mb-4">
                    <input type="hidden" name="tab" value="lote">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-6">
                            <label class="form-label">Lote (Orden Maestra)</label>
                            <select name="loteOm" class="form-select form-input">
                                <option value="">-- Seleccione un lote --</option>
                                <% if (listaLotes != null) {
                                    for (Lote l : listaLotes) { %>
                                        <option value="<%= l.getOm() %>"
                                            <%= l.getOm().equals(filtroLoteOm != null ? filtroLoteOm : "") ? "selected" : "" %>>
                                            <%= l.getOm() %> — Ref: <%= l.getReferenciaIdRef() %>
                                            (<%= l.getTotalUnidades() %> uds, Mód. <%= l.getModuloOperativoId() %>)
                                        </option>
                                <% }} %>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn-principal w-100">
                                <i class="fas fa-search me-1"></i>Ver avance
                            </button>
                        </div>
                    </div>
                </form>

                <% if (Boolean.TRUE.equals(hayDatosLote) && loteSeleccionado != null) { %>

                    <div class="row g-3 mb-4">
                        <div class="col-md-3">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#2563eb;">
                                    <%= loteSeleccionado.getOm() %>
                                </div>
                                <div class="informe-card-label">Orden Maestra</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#2563eb;">
                                    <%= loteSeleccionado.getReferenciaIdRef() %>
                                </div>
                                <div class="informe-card-label">Referencia</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#2563eb;">
                                    <%= loteSeleccionado.getTotalUnidades() %>
                                </div>
                                <div class="informe-card-label">Total unidades</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="informe-card-resumen">
                                <div class="informe-card-valor" style="color:#2563eb;">
                                    Mód. <%= loteSeleccionado.getModuloOperativoId() %>
                                </div>
                                <div class="informe-card-label">Módulo asignado</div>
                            </div>
                        </div>
                    </div>

                    <h6 class="mb-2"><i class="fas fa-tasks me-1"></i>Avance por operación</h6>
                    <% if (avanceOperaciones != null && !avanceOperaciones.isEmpty()) { %>
                    <div class="tabla-contenedor mb-4">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Operación</th>
                                    <th>SAM</th>
                                    <th>Pulsaciones realizadas</th>
                                    <th>Total unidades lote</th>
                                    <th>% completado</th>
                                    <th>Progreso</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Object[] fila : avanceOperaciones) {
                                    double pct = (Double) fila[4];
                                    String clsBarra = pct >= 100 ? "bg-success" : pct >= 60 ? "bg-warning" : "bg-danger";
                                %>
                                <tr>
                                    <td><strong><%= fila[0] %></strong></td>
                                    <td class="text-center text-muted"><%= String.format("%.3f", (Double) fila[1]) %></td>
                                    <td class="text-center"><%= fila[2] %></td>
                                    <td class="text-center text-muted"><%= fila[3] %></td>
                                    <td class="text-center"><strong><%= pct %>%</strong></td>
                                    <td style="min-width:120px;">
                                        <div class="progress" style="height:10px; border-radius:5px;">
                                            <div class="progress-bar <%= clsBarra %>"
                                                 role="progressbar"
                                                 style="width:<%= Math.min(pct, 100.0) %>%">
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                        <p class="text-muted mb-4">Sin pulsaciones registradas para este lote.</p>
                    <% } %>

                    <% if (pulsacionesPorOperario != null && !pulsacionesPorOperario.isEmpty()) { %>
                    <h6 class="mb-2"><i class="fas fa-users me-1"></i>Contribución por operario</h6>
                    <div class="tabla-contenedor">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Operario</th>
                                    <th>Cédula</th>
                                    <th>Pulsaciones totales</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Object[] fila : pulsacionesPorOperario) { %>
                                <tr>
                                    <td><%= fila[0] %></td>
                                    <td class="text-muted"><%= fila[1] %></td>
                                    <td class="text-center"><strong><%= fila[2] %></strong></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } %>

                <% } else if (filtroLoteOm != null && !filtroLoteOm.trim().isEmpty()) { %>
                    <p class="text-muted">Sin datos para el lote seleccionado.</p>
                <% } %>

            </div><%-- fin tab lote --%>

        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function cambiarTab(nombre, boton) {
            document.querySelectorAll('.tab-content').forEach(function(t) { t.style.display = 'none'; });
            document.querySelectorAll('.tab').forEach(function(t) { t.classList.remove('active'); });
            document.getElementById('tab-' + nombre).style.display = 'block';
            boton.classList.add('active');
        }
    </script>

</body>
</html>
