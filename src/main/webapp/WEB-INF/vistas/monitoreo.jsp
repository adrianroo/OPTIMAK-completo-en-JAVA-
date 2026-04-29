<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    Boolean soloTarjetas   = (Boolean) request.getAttribute("soloTarjetas");
    String  sesionNombre   = (String)  session.getAttribute("sesionNombre");

    List<Object[]>              estadoModulos     = (List<Object[]>)              request.getAttribute("estadoModulos");
    List<Object[]>              incidenciasHoy    = (List<Object[]>)              request.getAttribute("incidenciasHoy");
    Map<String, List<Object[]>> detallesPorModulo = (Map<String, List<Object[]>>) request.getAttribute("detallesPorModulo");
%>
<%!
    private String claseEficiencia(double ef, boolean activo) {
        if (!activo) return "monitoreo-eficiencia-sindata";
        if (ef >= 80) return "monitoreo-eficiencia-buena";
        if (ef >= 65) return "monitoreo-eficiencia-regular";
        return "monitoreo-eficiencia-baja";
    }
    private String clasesBadge(double ef, boolean activo) {
        if (!activo) return "badge-pausado";
        if (ef >= 80) return "badge-terminado";
        if (ef >= 65) return "badge-proceso";
        return "badge-pendiente";
    }
    private String textoNivel(double ef, boolean activo) {
        if (!activo) return "Sin actividad";
        if (ef >= 80) return "Buena";
        if (ef >= 65) return "Regular";
        return "Baja";
    }
%>

<%-- ================================================================
     MODO FRAGMENTO — solo las tarjetas del grid (para el polling JS)
================================================================ --%>
<% if (Boolean.TRUE.equals(soloTarjetas)) { %>

<% if (estadoModulos != null && !estadoModulos.isEmpty()) {
    for (Object[] fila : estadoModulos) {
        String moduloId     = (String)  fila[0];
        double eficiencia   = (Double)  fila[1];
        int    opActivos    = (Integer) fila[2];
        int    pulsaciones  = (Integer) fila[3];
        int    incidencias  = (Integer) fila[4];
        boolean tieneActividad = opActivos > 0;
        List<Object[]> detalle = detallesPorModulo != null ? detallesPorModulo.get(moduloId) : null;
%>
<div class="monitoreo-card <%= !tieneActividad ? "monitoreo-card-sinactividad" : "" %>">
    <div class="monitoreo-card-header">
        <span class="monitoreo-modulo-id">
            <i class="fas fa-layer-group me-1" style="color:#2563eb;font-size:16px;"></i>
            Módulo <%= moduloId %>
        </span>
        <span class="badge-estado <%= clasesBadge(eficiencia, tieneActividad) %>">
            <%= textoNivel(eficiencia, tieneActividad) %>
        </span>
    </div>
    <div class="monitoreo-eficiencia-bloque">
        <div class="monitoreo-eficiencia-numero <%= claseEficiencia(eficiencia, tieneActividad) %>">
            <%= tieneActividad ? String.format("%.1f", eficiencia) + "%" : "&#8212;" %>
        </div>
        <div style="font-size:11px;font-weight:700;color:#94a3b8;text-transform:uppercase;letter-spacing:0.5px;">
            Eficiencia promedio del turno
        </div>
    </div>
    <div class="monitoreo-stats">
        <div class="monitoreo-stat">
            <div class="monitoreo-stat-valor"><%= opActivos %></div>
            <div class="monitoreo-stat-label">Operarios activos</div>
        </div>
        <div class="monitoreo-stat">
            <div class="monitoreo-stat-valor"><%= pulsaciones %></div>
            <div class="monitoreo-stat-label">Pulsaciones hoy</div>
        </div>
        <div class="monitoreo-stat <%= incidencias > 0 ? "monitoreo-stat-alerta" : "" %>">
            <div class="monitoreo-stat-valor"><%= incidencias %></div>
            <div class="monitoreo-stat-label">
                <% if (incidencias > 0) { %><i class="fas fa-exclamation-triangle me-1"></i><% } %>
                Incidencias
            </div>
        </div>
    </div>
    <% if (detalle != null && !detalle.isEmpty()) { %>
    <button class="monitoreo-toggle-btn"
            onclick="toggleDetalle('detalle-<%= moduloId %>', this)">
        <i class="fas fa-chevron-down me-1"></i>Ver operarios
    </button>
    <div id="detalle-<%= moduloId %>" class="monitoreo-detalle">
        <div class="tabla-contenedor" style="margin-top:8px;">
            <table class="tabla" style="font-size:13px;">
                <thead>
                    <tr>
                        <th>Operario</th>
                        <th>Eficiencia</th>
                        <th>Pulsaciones</th>
                        <th>Lote actual</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Object[] op : detalle) {
                        double efOp   = (Double)  op[2];
                        int    pulsOp = (Integer) op[3];
                        String lote   = op[4] != null ? (String) op[4] : "&#8212;";
                        boolean opActivo = pulsOp > 0;
                        String clsBadgeOp = opActivo
                            ? (efOp >= 80 ? "badge-terminado" : efOp >= 65 ? "badge-proceso" : "badge-pendiente")
                            : "badge-pausado";
                    %>
                    <tr>
                        <td>
                            <span style="font-weight:600;"><%= op[0] %></span>
                            <div style="font-size:11px;color:#94a3b8;"><%= op[1] %></div>
                        </td>
                        <td>
                            <span class="badge-estado <%= clsBadgeOp %>">
                                <%= opActivo ? String.format("%.1f", efOp) + "%" : "Sin reg." %>
                            </span>
                        </td>
                        <td class="text-center"><%= pulsOp %></td>
                        <td style="font-size:12px;color:#64748b;"><%= lote %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <% } %>
</div>
<%  } // fin for fila
} else { %>
<div class="info-box" style="grid-column:1/-1;">
    <p><i class="fas fa-info-circle me-2"></i>No hay módulos registrados para esta empresa.</p>
</div>
<% } %>

<%-- ================================================================
     PÁGINA COMPLETA
================================================================ --%>
<% } else { %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Monitoreo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>

    <header class="header-principal">
        <div class="header-logo-container">
            <img src="img/logoreducido.png" alt="OPTIMAK" class="header-logo-img"
                 onerror="this.style.display='none'">
            <h1 class="header-titulo">OPTIMAK</h1>
        </div>
        <nav class="header-nav">
            <a href="admin-dashboard">Inicio</a>
            <a href="produccion">Producción</a>
            <a href="personal">Personal</a>
            <a href="informes">Informes</a>
            <a href="monitoreo" class="active">Monitoreo</a>
        </nav>
        <div class="header-usuario">
            <i class="fas fa-user-circle"></i>
            <span class="header-usuario-texto">Admin — <%= sesionNombre != null ? sesionNombre : "" %></span>
            <a href="logout" class="btn-icono" title="Cerrar sesión">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </header>

    <main class="contenedor-principal">
        <div class="tarjeta">

            <div class="monitoreo-header-bar">
                <div>
                    <h2 class="titulo-seccion" style="margin-bottom:8px;">Monitoreo en Tiempo Real</h2>
                    <p class="subtitulo-seccion" style="margin-bottom:0;">
                        Estado del turno actual - todos los módulos
                    </p>
                </div>
                <div style="display:flex;align-items:center;gap:16px;flex-wrap:wrap;">
                    <div class="monitoreo-indicador-vivo">
                        <div class="monitoreo-dot"></div>
                        <span>En vivo &nbsp;&middot;&nbsp; actualiza cada 30 s</span>
                    </div>
                    <div style="font-size:13px;color:#94a3b8;">
                        Última actualización: <strong id="hora-actualizacion">&#8212;</strong>
                    </div>
                    <button class="btn-secundario" style="padding:8px 16px;font-size:13px;"
                            onclick="actualizarAhora()">
                        <i class="fas fa-sync-alt me-1"></i>Actualizar ahora
                    </button>
                </div>
            </div>

            <%-- Grid de tarjetas — reemplazado por el polling cada 30 s --%>
            <div id="tarjetas-modulos" class="monitoreo-grid">

                <% if (estadoModulos != null && !estadoModulos.isEmpty()) {
                    for (Object[] fila : estadoModulos) {
                        String moduloId     = (String)  fila[0];
                        double eficiencia   = (Double)  fila[1];
                        int    opActivos    = (Integer) fila[2];
                        int    pulsaciones  = (Integer) fila[3];
                        int    incidencias  = (Integer) fila[4];
                        boolean tieneActividad = opActivos > 0;
                        List<Object[]> detalle = detallesPorModulo != null ? detallesPorModulo.get(moduloId) : null;
                %>
                <div class="monitoreo-card <%= !tieneActividad ? "monitoreo-card-sinactividad" : "" %>">
                    <div class="monitoreo-card-header">
                        <span class="monitoreo-modulo-id">
                            <i class="fas fa-layer-group me-1" style="color:#2563eb;font-size:16px;"></i>
                            Módulo <%= moduloId %>
                        </span>
                        <span class="badge-estado <%= clasesBadge(eficiencia, tieneActividad) %>">
                            <%= textoNivel(eficiencia, tieneActividad) %>
                        </span>
                    </div>
                    <div class="monitoreo-eficiencia-bloque">
                        <div class="monitoreo-eficiencia-numero <%= claseEficiencia(eficiencia, tieneActividad) %>">
                            <%= tieneActividad ? String.format("%.1f", eficiencia) + "%" : "&#8212;" %>
                        </div>
                        <div style="font-size:11px;font-weight:700;color:#94a3b8;text-transform:uppercase;letter-spacing:0.5px;">
                            Eficiencia promedio del turno
                        </div>
                    </div>
                    <div class="monitoreo-stats">
                        <div class="monitoreo-stat">
                            <div class="monitoreo-stat-valor"><%= opActivos %></div>
                            <div class="monitoreo-stat-label">Operarios activos</div>
                        </div>
                        <div class="monitoreo-stat">
                            <div class="monitoreo-stat-valor"><%= pulsaciones %></div>
                            <div class="monitoreo-stat-label">Pulsaciones hoy</div>
                        </div>
                        <div class="monitoreo-stat <%= incidencias > 0 ? "monitoreo-stat-alerta" : "" %>">
                            <div class="monitoreo-stat-valor"><%= incidencias %></div>
                            <div class="monitoreo-stat-label">
                                <% if (incidencias > 0) { %><i class="fas fa-exclamation-triangle me-1"></i><% } %>
                                Incidencias
                            </div>
                        </div>
                    </div>
                    <% if (detalle != null && !detalle.isEmpty()) { %>
                    <button class="monitoreo-toggle-btn"
                            onclick="toggleDetalle('detalle-<%= moduloId %>', this)">
                        <i class="fas fa-chevron-down me-1"></i>Ver operarios
                    </button>
                    <div id="detalle-<%= moduloId %>" class="monitoreo-detalle">
                        <div class="tabla-contenedor" style="margin-top:8px;">
                            <table class="tabla" style="font-size:13px;">
                                <thead>
                                    <tr>
                                        <th>Operario</th>
                                        <th>Eficiencia</th>
                                        <th>Pulsaciones</th>
                                        <th>Lote actual</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Object[] op : detalle) {
                                        double efOp   = (Double)  op[2];
                                        int    pulsOp = (Integer) op[3];
                                        String lote   = op[4] != null ? (String) op[4] : "&#8212;";
                                        boolean opActivo = pulsOp > 0;
                                        String clsBadgeOp = opActivo
                                            ? (efOp >= 80 ? "badge-terminado" : efOp >= 65 ? "badge-proceso" : "badge-pendiente")
                                            : "badge-pausado";
                                    %>
                                    <tr>
                                        <td>
                                            <span style="font-weight:600;"><%= op[0] %></span>
                                            <div style="font-size:11px;color:#94a3b8;"><%= op[1] %></div>
                                        </td>
                                        <td>
                                            <span class="badge-estado <%= clsBadgeOp %>">
                                                <%= opActivo ? String.format("%.1f", efOp) + "%" : "Sin reg." %>
                                            </span>
                                        </td>
                                        <td class="text-center"><%= pulsOp %></td>
                                        <td style="font-size:12px;color:#64748b;"><%= lote %></td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } %>
                </div>
                <%  } // fin for fila
                } else { %>
                <div class="info-box" style="grid-column:1/-1;">
                    <p><i class="fas fa-info-circle me-2"></i>No hay módulos registrados para esta empresa.</p>
                </div>
                <% } %>

            </div><%-- fin #tarjetas-modulos --%>

            <%-- Tabla de incidencias del turno --%>
            <% if (incidenciasHoy != null && !incidenciasHoy.isEmpty()) { %>
            <h5 class="mb-3 mt-4" style="color:#dc2626;">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Incidencias del turno (<%= incidenciasHoy.size() %>
                registrada<%= incidenciasHoy.size() != 1 ? "s" : "" %>)
            </h5>
            <div class="tabla-contenedor">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Hora</th>
                            <th>Operario</th>
                            <th>Módulo</th>
                            <th>Eficiencia que generó alerta</th>
                            <th>Justificación del operario</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Object[] inc : incidenciasHoy) {
                            double efInc = (Double) inc[4];
                            String clsInc = efInc >= 80 ? "badge-terminado" : efInc >= 65 ? "badge-proceso" : "badge-pendiente";
                            String horaStr = inc[0] != null ? inc[0].toString() : "";
                            if (horaStr.length() >= 16) horaStr = horaStr.substring(11, 16);
                        %>
                        <tr>
                            <td style="font-weight:700;white-space:nowrap;"><%= horaStr %></td>
                            <td><%= inc[1] %></td>
                            <td class="text-center">
                                <span class="badge-estado badge-en-proceso">Mód. <%= inc[2] %></span>
                            </td>
                            <td class="text-center">
                                <span class="badge-estado <%= clsInc %>">
                                    <%= String.format("%.1f", efInc) %>%
                                </span>
                            </td>
                            <td style="font-size:13px;color:#334155;"><%= inc[3] %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
            <div class="alerta-info mt-4" style="color:#059669;background:#ecfdf5;border-color:#6ee7b7;">
                <i class="fas fa-check-circle me-2"></i>
                Sin incidencias registradas en el turno de hoy.
            </div>
            <% } %>

        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleDetalle(idDiv, boton) {
            var div = document.getElementById(idDiv);
            if (!div) return;
            var abierto = div.classList.contains('abierto');
            div.classList.toggle('abierto', !abierto);
            boton.innerHTML = abierto
                ? '<i class="fas fa-chevron-down me-1"></i>Ver operarios'
                : '<i class="fas fa-chevron-up me-1"></i>Ocultar operarios';
        }

        function marcarHora() {
            var ahora = new Date();
            var hh = String(ahora.getHours()).padStart(2, '0');
            var mm = String(ahora.getMinutes()).padStart(2, '0');
            var ss = String(ahora.getSeconds()).padStart(2, '0');
            document.getElementById('hora-actualizacion').textContent = hh + ':' + mm + ':' + ss;
        }

        function actualizarTarjetas() {
            fetch('monitoreo?async=true')
                .then(function(r) { return r.text(); })
                .then(function(html) {
                    document.getElementById('tarjetas-modulos').innerHTML = html;
                    marcarHora();
                })
                .catch(function() {
                    // falla silenciosa — se reintenta en el próximo ciclo
                });
        }

        function actualizarAhora() {
            actualizarTarjetas();
        }

        marcarHora();
        setInterval(actualizarTarjetas, 30000);
    </script>

</body>
</html>
<% } %>
