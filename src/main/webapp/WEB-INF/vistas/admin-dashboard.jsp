<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="co.optimak.modelo.Empresa" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Panel Administrativo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

    <%
        String nombre   = (String) session.getAttribute("sesionNombre");
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
            <a href="${pageContext.request.contextPath}/admin-dashboard" class="active">Inicio</a>
            <a href="${pageContext.request.contextPath}/produccion">Producción</a>
            <a href="${pageContext.request.contextPath}/personal">Personal</a>
            <a href="${pageContext.request.contextPath}/informes">Informes</a>
            <a href="${pageContext.request.contextPath}/monitoreo">Monitoreo</a>
        </nav>

        <div class="header-usuario">
            <i class="fas fa-user-circle"></i>
            <span class="header-usuario-texto">Admin — <%= nombre %></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-icono" title="Cerrar sesión">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </header>

    <main class="contenedor-principal">

        <div class="tarjeta">
            <h2 class="titulo-seccion">Bienvenido</h2>
            <p class="subtitulo-seccion">Rol Actual: Administrador</p>

            <div class="dashboard-grid">

                <div class="dashboard-card text-center">
                    <img src="${pageContext.request.contextPath}/img/logoazulcompleto.png"
                         alt="OPTIMAK"
                         style="max-width: 300px; margin-bottom: 20px;"
                         onerror="this.style.display='none'">
                </div>

                <div class="info-panel">
                    <p><strong>Bienvenido:</strong> <%= nombre %></p>
                    <p><strong>Rol:</strong> Administrador</p>
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
            <h3 class="titulo-seccion">Accesos Rápidos</h3>
            <div class="cards-grid">

                <div class="card" onclick="window.location.href='${pageContext.request.contextPath}/produccion'">
                    <div class="text-center">
                        <i class="fas fa-box" style="font-size: 48px; color: #2563eb; margin-bottom: 15px;"></i>
                        <h4 style="font-size: 16px; font-weight: 700; color: #334155;">Gestión de Producción</h4>
                        <p style="font-size: 13px; color: #64748b; margin-top: 10px;">Administrar lotes, referencias y operaciones</p>
                    </div>
                </div>

                <div class="card" onclick="window.location.href='${pageContext.request.contextPath}/personal'">
                    <div class="text-center">
                        <i class="fas fa-users" style="font-size: 48px; color: #2563eb; margin-bottom: 15px;"></i>
                        <h4 style="font-size: 16px; font-weight: 700; color: #334155;">Gestión de Personal</h4>
                        <p style="font-size: 13px; color: #64748b; margin-top: 10px;">Administrar empleados y módulos operativos</p>
                    </div>
                </div>

                <div class="card" onclick="window.location.href='${pageContext.request.contextPath}/informes'">
                    <div class="text-center">
                        <i class="fas fa-chart-line" style="font-size: 48px; color: #2563eb; margin-bottom: 15px;"></i>
                        <h4 style="font-size: 16px; font-weight: 700; color: #334155;">Informes de Eficiencia</h4>
                        <p style="font-size: 13px; color: #64748b; margin-top: 10px;">Consultar reportes históricos y análisis</p>
                    </div>
                </div>

                <div class="card" onclick="window.location.href='${pageContext.request.contextPath}/monitoreo'">
                    <div class="text-center">
                        <i class="fas fa-desktop" style="font-size: 48px; color: #2563eb; margin-bottom: 15px;"></i>
                        <h4 style="font-size: 16px; font-weight: 700; color: #334155;">Monitoreo en Tiempo Real</h4>
                        <p style="font-size: 13px; color: #64748b; margin-top: 10px;">Ver eficiencia actual de todos los módulos</p>
                    </div>
                </div>

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
