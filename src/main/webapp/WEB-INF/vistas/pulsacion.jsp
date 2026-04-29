<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, co.optimak.modelo.Lote, co.optimak.modelo.Operacion, co.optimak.modelo.RegistroEficiencia" %>
<%
    Lote lote                 = (Lote) request.getAttribute("lote");
    Operacion operacion       = (Operacion) request.getAttribute("operacion");
    String eficienciaInd      = (String) request.getAttribute("eficienciaIndividual");
    String eficienciaMod      = (String) request.getAttribute("eficienciaModular");
    int pulsacionesHoy        = (Integer) request.getAttribute("pulsacionesHoy");
    List<RegistroEficiencia> registrosHoy = (List<RegistroEficiencia>) request.getAttribute("registrosHoy");
    String alertaEficiencia   = (String) request.getAttribute("alertaEficiencia");
    Long alertaRegId          = (Long) request.getAttribute("alertaEficienciaRegId");
    String alertaCuello       = (String) request.getAttribute("alertaCuelloBotella");
    String errorPulsacion     = (String) request.getAttribute("errorPulsacion");
    String moduloId           = (String) request.getAttribute("sesionModulo");
    String nombre             = (String) request.getAttribute("sesionNombre");
    String ctx                = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Registro de Pulsaciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
</head>
<body>

    <header class="header-principal">
        <div class="header-logo-container">
            <img src="<%= ctx %>/img/logoreducido.png" alt="OPTIMAK" class="header-logo-img">
            <h1 class="header-titulo">OPTIMAK</h1>
        </div>
        <nav class="header-nav">
            <a href="<%= ctx %>/seleccion-lote">Lotes</a>
            <a href="<%= ctx %>/operario-dashboard">Inicio</a>
            <a href="<%= ctx %>/seleccion-operacion">Operaciones</a>
        </nav>
        <div class="header-usuario">
            <span class="badge-modulo">MÓDULO <%= moduloId %></span>
            <i class="fas fa-user-circle"></i>
            <span class="header-usuario-texto"><%= nombre %></span>
            <a href="<%= ctx %>/logout" class="btn-icono" title="Cerrar sesión">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </header>

    <main class="contenedor-principal">

        <!-- Error de pulsación (si existe) -->
        <% if (errorPulsacion != null) { %>
        <div class="alert alert-danger" style="margin-bottom: 16px; font-size: 14px;">
            <i class="fas fa-exclamation-circle" style="margin-right: 8px;"></i>
            <%= errorPulsacion %>
        </div>
        <% } %>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; align-items: start;">

            <!-- Panel izquierdo: info y eficiencia -->
            <div>

                <div style="margin-bottom: 16px;">
                    <a href="<%= ctx %>/seleccion-operacion" class="btn-secundario">
                        <i class="fas fa-exchange-alt" style="margin-right: 6px;"></i>
                        Cambiar Lote / Operación
                    </a>
                </div>

                <!-- Info del trabajo actual -->
                <div class="pulsacion-info-panel">
                    <div class="pulsacion-info-fila">
                        <span class="pulsacion-info-label">LOTE</span>
                        <span class="pulsacion-info-valor"><%= lote.getOm() %></span>
                    </div>
                    <div class="pulsacion-info-fila">
                        <span class="pulsacion-info-label">OPERACIÓN</span>
                        <span class="pulsacion-info-valor"><%= operacion.getNombreCorto() %></span>
                    </div>
                    <div class="pulsacion-info-fila">
                        <span class="pulsacion-info-label">SAM OBJETIVO</span>
                        <span class="pulsacion-info-valor"><%= String.format("%.3f", operacion.getSamOperacion()) %> min</span>
                    </div>
                </div>

                <!-- Indicadores de eficiencia -->
                <div class="cards-grid" style="grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 16px;">

                    <div class="eficiencia-card">
                        <div class="eficiencia-label">MI EFICIENCIA</div>
                        <div class="eficiencia-valor" id="mi-eficiencia"><%= eficienciaInd %>%</div>
                        <div class="eficiencia-estado" id="estado-individual">
                            <%
                                double eInd = 0;
                                try { eInd = Double.parseDouble(eficienciaInd); } catch (Exception ex) {}
                                if (eInd == 0) { %>Sin datos<% }
                                else if (eInd < 65) { %><span style="color:#ef4444;">Por debajo</span><% }
                                else if (eInd < 85) { %><span style="color:#f59e0b;">Regular</span><% }
                                else { %><span style="color:#22c55e;">Bien</span><% } %>
                        </div>
                    </div>

                    <div class="eficiencia-card">
                        <div class="eficiencia-label">EFICIENCIA MÓDULO</div>
                        <div class="eficiencia-valor" id="eficiencia-modulo"><%= eficienciaMod %>%</div>
                        <div class="eficiencia-estado" id="estado-modulo">
                            <%
                                double eMod = 0;
                                try { eMod = Double.parseDouble(eficienciaMod); } catch (Exception ex2) {}
                                if (eMod == 0) { %>Sin datos<% }
                                else if (eMod < 65) { %><span style="color:#ef4444;">Por debajo</span><% }
                                else if (eMod < 85) { %><span style="color:#f59e0b;">Regular</span><% }
                                else { %><span style="color:#22c55e;">Bien</span><% } %>
                        </div>
                    </div>

                </div>

                <!-- Botón ver resumen del día -->
                <div style="margin-top: 16px;">
                    <button class="btn-secundario" style="width: 100%;" onclick="abrirModalResumen()">
                        <i class="fas fa-calendar-day" style="margin-right: 8px;"></i>
                        Ver Resumen de Hoy
                    </button>
                </div>

            </div>

            <!-- Panel derecho: botón de pulsación -->
            <div class="pulsacion-panel-derecho">

                <!-- Contador unidades -->
                <div class="unidades-completadas-caja">
                    <div class="unidades-label">PULSACIONES HOY</div>
                    <div class="unidades-numero" id="contador-unidades"><%= pulsacionesHoy %></div>
                </div>

                <!-- Botón principal de pulsación -->
                <form method="post" action="<%= ctx %>/pulsacion" id="form-pulsacion">
                    <input type="hidden" name="accion" value="pulsar">
                    <button type="submit" class="btn-pulsacion" id="btn-pulsacion">
                        <i class="fas fa-hand-pointer"></i>
                        <span>PULSACIÓN</span>
                    </button>
                </form>

                <!-- Tiempo última pulsación (cosmético) -->
                <div class="tiempo-ultima-pulsacion">
                    <span>TURNO ACTUAL:</span>
                    <span id="tiempo-ultima" style="font-weight: 700; margin-left: 8px;">
                        <%= lote.getOm() %> — <%= operacion.getNombreCorto() %>
                    </span>
                </div>

            </div>

        </div>

    </main>

    <!-- Modal: Resumen del día -->
    <div class="modal-overlay" id="modal-resumen" style="display: none;">
        <div class="modal-contenido" style="max-width: 600px;">
            <div class="modal-header">
                <h4 class="modal-titulo">
                    <i class="fas fa-calendar-day" style="color: #2563eb; margin-right: 8px;"></i>
                    Resumen de Hoy
                </h4>
                <button class="btn-icono" onclick="cerrarModal('modal-resumen')">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px;">
                    <div class="eficiencia-card">
                        <div class="eficiencia-label">MI EFICIENCIA HOY</div>
                        <div class="eficiencia-valor"><%= eficienciaInd %>%</div>
                        <div class="eficiencia-estado">Acumulado</div>
                    </div>
                    <div class="eficiencia-card">
                        <div class="eficiencia-label">EFICIENCIA MÓDULO HOY</div>
                        <div class="eficiencia-valor"><%= eficienciaMod %>%</div>
                        <div class="eficiencia-estado">Acumulado</div>
                    </div>
                </div>

                <h5 style="font-size: 14px; font-weight: 600; color: #334155; margin-bottom: 12px;">
                    <i class="fas fa-clock" style="margin-right: 6px; color: #2563eb;"></i>
                    Registros de Eficiencia del Día
                </h5>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>Inicio</th>
                                <th>Fin</th>
                                <th>Mi Eficiencia</th>
                                <th>Unidades</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% if (registrosHoy == null || registrosHoy.isEmpty()) { %>
                            <tr>
                                <td colspan="4" style="text-align: center; color: #94a3b8; padding: 24px;">
                                    No hay registros aún. Se guardan cada 20 minutos productivos.
                                </td>
                            </tr>
                        <% } else {
                            for (RegistroEficiencia reg : registrosHoy) {
                                String inicio = reg.getPeriodoInicio() != null ? reg.getPeriodoInicio().substring(11, 16) : "—";
                                String fin    = reg.getPeriodoFin()    != null ? reg.getPeriodoFin().substring(11, 16)    : "—";
                        %>
                            <tr>
                                <td><%= inicio %></td>
                                <td><%= fin %></td>
                                <td><%= String.format("%.1f", reg.getEficienciaPorcentaje()) %>%</td>
                                <td><%= reg.getCantidadProducida() %></td>
                            </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>

            </div>
            <div class="modal-footer">
                <button class="btn-secundario" onclick="cerrarModal('modal-resumen')">Cerrar</button>
            </div>
        </div>
    </div>

    <!-- Modal: Alerta de eficiencia baja -->
    <div class="modal-overlay" id="modal-alerta-eficiencia" style="display: none;">
        <div class="modal-contenido" style="max-width: 480px;">
            <div class="modal-header" style="background: #ef4444; border-radius: 8px 8px 0 0;">
                <h4 class="modal-titulo" style="color: #fff;">
                    <i class="fas fa-bell" style="margin-right: 8px;"></i>
                    Alerta de Eficiencia
                </h4>
                <button class="btn-icono" style="color: #fff;" onclick="cerrarModal('modal-alerta-eficiencia')">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <span class="badge-notificacion badge-personal" style="margin-bottom: 16px; display: inline-block;">
                    NOTIFICACIÓN PERSONAL
                </span>
                <div class="alerta-eficiencia-caja">
                    <div class="alerta-porcentaje" id="alerta-porcentaje">
                        <%= alertaEficiencia != null ? alertaEficiencia + "%" : "" %>
                    </div>
                    <div>
                        <div style="font-weight: 600; color: #334155; font-size: 14px;">
                            Tu eficiencia actual está por debajo del mínimo
                        </div>
                        <div style="font-size: 12px; color: #64748b; margin-top: 4px;">
                            Mínimo requerido: 65%
                        </div>
                    </div>
                </div>
                <form method="post" action="<%= ctx %>/pulsacion" id="form-incidencia">
                    <input type="hidden" name="accion" value="incidencia">
                    <% if (alertaRegId != null) { %>
                    <input type="hidden" name="regId" value="<%= alertaRegId %>">
                    <% } %>
                    <div class="form-grupo" style="margin-top: 16px;">
                        <label class="form-label">
                            Justificación <span style="color: #f59e0b; font-weight: 400;">(Opcional)</span>
                        </label>
                        <textarea class="form-input" name="descripcion" rows="4"
                            placeholder="Describe la razón de la baja eficiencia (ej: falla de máquina, material defectuoso, pausa médica, etc.)"></textarea>
                        <p style="font-size: 12px; color: #94a3b8; margin-top: 8px;">
                            Esta información ayudará al supervisor a entender la situación.
                        </p>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn-secundario" onclick="cerrarModal('modal-alerta-eficiencia')">Cerrar</button>
                <button class="btn-principal" onclick="document.getElementById('form-incidencia').submit()">
                    <i class="fas fa-paper-plane" style="margin-right: 6px;"></i>
                    Enviar Justificación
                </button>
            </div>
        </div>
    </div>

    <!-- Modal: Cuello de botella (solo informativo) -->
    <div class="modal-overlay" id="modal-cuello-botella" style="display: none;">
        <div class="modal-contenido" style="max-width: 480px;">
            <div class="modal-header" style="background: #f59e0b; border-radius: 8px 8px 0 0;">
                <h4 class="modal-titulo" style="color: #fff;">
                    <i class="fas fa-exclamation-triangle" style="margin-right: 8px;"></i>
                    Cuello de Botella Detectado
                </h4>
                <button class="btn-icono" style="color: #fff;" onclick="cerrarModal('modal-cuello-botella')">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <span class="badge-notificacion badge-general" style="margin-bottom: 16px; display: inline-block;">
                    NOTIFICACIÓN GENERAL
                </span>
                <p style="font-size: 14px; color: #334155; margin-bottom: 12px;">
                    Hay un cuello de botella en la operación:
                </p>
                <div class="cuello-operacion-caja">
                    <i class="fas fa-exclamation-circle" style="color: #f59e0b; margin-right: 8px;"></i>
                    <span style="font-weight: 600; color: #334155;" id="cuello-nombre-operacion">
                        <%= alertaCuello != null ? alertaCuello : "" %>
                    </span>
                </div>
                <div class="cuello-info-caja">
                    <i class="fas fa-lightbulb" style="color: #f59e0b; margin-right: 8px;"></i>
                    <span style="font-size: 13px; color: #64748b;">
                        Si te es posible, puedes apoyar para contribuir al flujo de trabajo del módulo.
                    </span>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn-secundario" onclick="cerrarModal('modal-cuello-botella')">Cerrar</button>
            </div>
        </div>
    </div>

    <script>
        function abrirModalResumen() {
            document.getElementById('modal-resumen').style.display = 'flex';
        }
        function cerrarModal(id) {
            document.getElementById(id).style.display = 'none';
        }

        // Abrir alertas automáticamente si el servidor las activó
        window.onload = function() {
            <% if (alertaEficiencia != null) { %>
            document.getElementById('modal-alerta-eficiencia').style.display = 'flex';
            <% } %>
            <% if (alertaCuello != null) { %>
            document.getElementById('modal-cuello-botella').style.display = 'flex';
            <% } %>
        };

        // Evitar doble submit del botón de pulsación
        document.getElementById('form-pulsacion').addEventListener('submit', function() {
            document.getElementById('btn-pulsacion').disabled = true;
        });
    </script>

</body>
</html>
