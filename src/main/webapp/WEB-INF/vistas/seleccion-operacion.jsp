<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, co.optimak.modelo.Lote, co.optimak.modelo.Operacion" %>
<%
    Lote lote                 = (Lote) request.getAttribute("lote");
    List<Operacion> operaciones = (List<Operacion>) request.getAttribute("operaciones");
    String moduloId           = (String) request.getAttribute("sesionModulo");
    String nombre             = (String) request.getAttribute("sesionNombre");
    String ctx                = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Selección de Operación</title>
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
            <a href="<%= ctx %>/operario-dashboard">Inicio</a>
            <a href="<%= ctx %>/seleccion-lote">Lotes</a>
            <a href="<%= ctx %>/seleccion-operacion" class="active">Operaciones</a>
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

        <div class="tarjeta">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                <div>
                    <h2 class="titulo-seccion">Selecciona una Operación</h2>
                    <p class="subtitulo-seccion">Elige la operación que realizarás</p>
                </div>
                <a href="<%= ctx %>/seleccion-lote" class="btn-secundario">
                    <i class="fas fa-arrow-left" style="margin-right: 6px;"></i>
                    Volver a Lotes
                </a>
            </div>

            <div class="pasos-indicador">
                <div class="paso paso-completado">
                    <div class="paso-numero"><i class="fas fa-check"></i></div>
                    <span class="paso-texto">Seleccionar Lote</span>
                </div>
                <div class="paso-linea paso-linea-completada"></div>
                <div class="paso paso-activo">
                    <div class="paso-numero">2</div>
                    <span class="paso-texto">Seleccionar Operación</span>
                </div>
                <div class="paso-linea"></div>
                <div class="paso paso-inactivo">
                    <div class="paso-numero">3</div>
                    <span class="paso-texto">Registrar Pulsaciones</span>
                </div>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 280px 1fr; gap: 20px; align-items: start;">

            <!-- Panel izquierdo: info del lote seleccionado -->
            <div>
                <div class="tarjeta" style="margin-bottom: 0;">
                    <h3 class="titulo-seccion" style="font-size: 14px; margin-bottom: 12px;">
                        <i class="fas fa-box" style="margin-right: 6px; color: #2563eb;"></i>
                        Lote Seleccionado
                    </h3>
                    <div style="font-size: 13px; color: #64748b;">
                        <p style="margin-bottom: 6px;">
                            <strong style="color: #334155;">OM:</strong>
                            <%= lote.getOm() %>
                        </p>
                        <p style="margin-bottom: 6px;">
                            <strong style="color: #334155;">Referencia:</strong>
                            <%= lote.getReferenciaIdRef() %>
                        </p>
                        <p style="margin-bottom: 6px;">
                            <strong style="color: #334155;">Color:</strong>
                            <%= lote.getColor() %>
                        </p>
                        <p style="margin-bottom: 0;">
                            <strong style="color: #334155;">Unidades:</strong>
                            <%= lote.getTotalUnidades() %>
                        </p>
                    </div>
                    <hr style="margin: 16px 0; border-color: #e2e8f0;">
                    <h3 class="titulo-seccion" style="font-size: 14px; margin-bottom: 8px;">
                        <i class="fas fa-tasks" style="margin-right: 6px; color: #2563eb;"></i>
                        Selecciona una Operación
                    </h3>
                    <p style="font-size: 12px; color: #94a3b8;">
                        Haz clic en el botón <i class="fas fa-play"></i> de la operación para comenzar.
                    </p>
                </div>
            </div>

            <!-- Lista de operaciones -->
            <div class="tarjeta" style="margin-bottom: 0;">
                <h3 class="titulo-seccion" style="font-size: 16px; margin-bottom: 16px;">
                    <i class="fas fa-list-ol" style="margin-right: 8px; color: #2563eb;"></i>
                    Operaciones de la Referencia
                </h3>

                <div id="lista-operaciones">
                <% if (operaciones == null || operaciones.isEmpty()) { %>
                    <p style="color: #94a3b8; font-size: 14px; text-align: center; padding: 24px;">
                        No hay operaciones configuradas para esta referencia.
                    </p>
                <% } else {
                    int num = 0;
                    for (Operacion op : operaciones) {
                        num++;
                %>
                    <div class="operacion-card" onclick="resaltarCard(this)">
                        <div class="operacion-numero"><%= num %></div>
                        <div class="operacion-info">
                            <div class="operacion-nombre"><%= op.getNombreCorto() %></div>
                            <div class="operacion-detalle"><%= op.getDetalle() != null ? op.getDetalle() : "" %></div>
                            <div class="operacion-maquina">
                                <i class="fas fa-cog" style="margin-right: 4px; color: #94a3b8;"></i>
                                <span><%= op.getMaquina() != null ? op.getMaquina() : "—" %></span>
                            </div>
                        </div>
                        <div class="operacion-sam">
                            <span class="sam-valor"><%= String.format("%.3f", op.getSamOperacion()) %></span>
                            <span class="sam-label">SAM</span>
                        </div>
                        <div class="operacion-accion">
                            <form method="post" action="<%= ctx %>/seleccion-operacion">
                                <input type="hidden" name="operacionId" value="<%= op.getIdOperacion() %>">
                                <button type="submit" class="btn-principal btn-sm">
                                    <i class="fas fa-play"></i>
                                </button>
                            </form>
                        </div>
                    </div>
                <% } } %>
                </div>
            </div>

        </div>

    </main>

    <script>
        function resaltarCard(card) {
            document.querySelectorAll('.operacion-card').forEach(function(c) {
                c.classList.remove('operacion-card-activa');
            });
            card.classList.add('operacion-card-activa');
        }
    </script>

</body>
</html>
