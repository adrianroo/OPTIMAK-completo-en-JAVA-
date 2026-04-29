<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="co.optimak.modelo.Empresa" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Panel Operario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

    <%
        String nombre   = (String) session.getAttribute("sesionNombre");
        String modulo   = (String) session.getAttribute("sesionModulo");
        Empresa empresa = (Empresa) request.getAttribute("empresa");
        String empNombre  = empresa != null ? empresa.getNombre()   : "—";
        String empNit     = empresa != null ? empresa.getNit()       : "—";
        String empDir     = empresa != null ? empresa.getDireccion() : "—";
        String empCiudad  = empresa != null ? empresa.getCiudad()   : "—";
    %>

    <header class="header-principal">
        <div class="header-logo-container">
            <img src="${pageContext.request.contextPath}/img/logoreducido.png"
                 alt="OPTIMAK" class="header-logo-img"
                 onerror="this.style.display='none'">
            <h1 class="header-titulo">OPTIMAK</h1>
        </div>

        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/operario-dashboard" class="active">Inicio</a>
            <a href="${pageContext.request.contextPath}/seleccion-lote">Lotes</a>
        </nav>

        <div class="header-usuario">
            <span class="badge-modulo">MÓDULO <%= modulo != null ? modulo : "—" %></span>
            <i class="fas fa-user-circle"></i>
            <span class="header-usuario-texto"><%= nombre %></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-icono" title="Cerrar sesión">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </header>

    <main class="contenedor-principal">

        <div class="tarjeta">
            <h2 class="titulo-seccion">Bienvenido</h2>
            <p class="subtitulo-seccion">Rol Actual: Operario</p>

            <div class="dashboard-grid">

                <div class="cards-grid" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));">

                    <div class="card text-center"
                         onclick="window.location.href='${pageContext.request.contextPath}/seleccion-lote'">
                        <i class="fas fa-boxes" style="font-size: 42px; color: #2563eb; margin-bottom: 12px;"></i>
                        <h4 style="font-size: 15px; font-weight: 700; color: #334155;">Selección de Lotes</h4>
                        <p style="font-size: 12px; color: #64748b; margin-top: 8px;">Elige el lote sobre el que trabajarás</p>
                    </div>

                </div>

                <div class="info-panel">
                    <p><strong>Bienvenido:</strong> <%= nombre %></p>
                    <p><strong>Rol:</strong> Operario</p>
                    <p><strong>Módulo:</strong> Módulo <%= modulo != null ? modulo : "—" %></p>
                    <p><strong>Fecha:</strong> <span id="fecha-actual"></span></p>
                    <hr style="border-color: rgba(255,255,255,0.2);">
                    <p><strong>Empresa:</strong> <%= empNombre %></p>
                    <p><strong>NIT:</strong> <%= empNit %></p>
                    <p><strong>Dirección:</strong> <%= empDir %></p>
                    <p><strong>Ciudad:</strong> <%= empCiudad %></p>
                </div>

            </div>
        </div>

        <div class="tarjeta">
            <h3 class="titulo-seccion" style="font-size: 16px; margin-bottom: 16px;">
                <i class="fas fa-box-open" style="margin-right: 8px; color: #2563eb;"></i>
                Lote Activo
            </h3>
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px;">
                <p style="color: #94a3b8; font-size: 14px; text-align: center; padding: 12px;">
                    <i class="fas fa-info-circle" style="margin-right: 6px;"></i>
                    No hay un lote activo en este momento.
                    <a href="${pageContext.request.contextPath}/seleccion-lote"
                       style="color: #2563eb; font-weight: 600; margin-left: 6px;">Seleccionar lote</a>
                </p>
            </div>
        </div>

    </main>

    <script>
        const fechaHoy = new Date();
        const opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('fecha-actual').textContent = fechaHoy.toLocaleDateString('es-CO', opciones);
    </script>

</body>
</html>
