<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, co.optimak.modelo.Lote" %>
<%
    List<Lote> lotes = (List<Lote>) request.getAttribute("lotes");
    String moduloId  = (String) request.getAttribute("sesionModulo");
    String nombre    = (String) request.getAttribute("sesionNombre");
    String ctx       = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Selección de Lote</title>
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
            <a href="<%= ctx %>/seleccion-lote" class="active">Lotes</a>
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

        <div class="tarjeta">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                <div>
                    <h2 class="titulo-seccion">Selecciona un Lote</h2>
                    <p class="subtitulo-seccion">Elige el lote sobre el cual trabajarás</p>
                </div>
                <a href="<%= ctx %>/operario-dashboard" class="btn-secundario">
                    <i class="fas fa-arrow-left" style="margin-right: 6px;"></i>
                    Volver al Inicio
                </a>
            </div>

            <div class="pasos-indicador">
                <div class="paso paso-activo">
                    <div class="paso-numero">1</div>
                    <span class="paso-texto">Seleccionar Lote</span>
                </div>
                <div class="paso-linea"></div>
                <div class="paso paso-inactivo">
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

        <div class="alerta-info">
            <i class="fas fa-info-circle" style="margin-right: 8px; color: #2563eb;"></i>
            <span>Solo puedes ver los lotes asignados a tu módulo. Selecciona uno para continuar.</span>
        </div>

        <div class="tarjeta">
            <h3 class="titulo-seccion" style="font-size: 16px; margin-bottom: 16px;">
                <i class="fas fa-boxes" style="margin-right: 8px; color: #2563eb;"></i>
                Lotes Disponibles
            </h3>

            <% if (lotes == null || lotes.isEmpty()) { %>
            <p style="color: #94a3b8; font-size: 14px; text-align: center; padding: 32px;">
                <i class="fas fa-box-open" style="font-size: 32px; display: block; margin-bottom: 12px;"></i>
                No hay lotes asignados a tu módulo.
            </p>
            <% } else { %>
            <% for (Lote lote : lotes) { %>
            <div class="lote-card">
                <div class="lote-card-header">
                    <span class="lote-om"><%= lote.getOm() %></span>
                    <span class="badge-estado badge-en-proceso">Disponible</span>
                </div>
                <div class="lote-card-body">
                    <div class="lote-dato">
                        <span class="lote-dato-label">Referencia</span>
                        <span class="lote-dato-valor"><%= lote.getReferenciaIdRef() %></span>
                    </div>
                    <div class="lote-dato">
                        <span class="lote-dato-label">Color</span>
                        <span class="lote-dato-valor"><%= lote.getColor() %></span>
                    </div>
                    <div class="lote-dato">
                        <span class="lote-dato-label">Total Unidades</span>
                        <span class="lote-dato-valor"><%= lote.getTotalUnidades() %></span>
                    </div>
                    <div class="lote-dato">
                        <span class="lote-dato-label">Fecha Ingreso</span>
                        <span class="lote-dato-valor"><%= lote.getFechaIngresoPlanita() %></span>
                    </div>
                </div>
                <div class="lote-card-footer">
                    <form method="post" action="<%= ctx %>/seleccion-lote">
                        <input type="hidden" name="loteOm" value="<%= lote.getOm() %>">
                        <button type="submit" class="btn-principal" style="width: 100%; margin-top: 12px;">
                            <i class="fas fa-check" style="margin-right: 6px;"></i>
                            Seleccionar este Lote
                        </button>
                    </form>
                </div>
            </div>
            <% } %>
            <% } %>
        </div>

    </main>

</body>
</html>
