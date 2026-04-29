<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Inicio de Sesión</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

    <div class="login-container">
        <div class="login-box">

            <img src="${pageContext.request.contextPath}/img/logoazulcompleto.png"
                 alt="OPTIMAK" class="login-logo"
                 onerror="this.style.display='none'">

            <h1 class="login-titulo">Bienvenido a OPTIMAK</h1>
            <p class="login-subtitulo">Ingresa tus credenciales para continuar</p>

            <%-- Mensaje de error si las credenciales fallaron --%>
            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
                <div class="alert alert-danger" role="alert" style="border-radius: 8px; font-size: 14px;">
                    <i class="fas fa-exclamation-circle me-2"></i><%= error %>
                </div>
            <% } %>

            <form method="post" action="login">

                <div class="form-grupo">
                    <label class="form-label">Número de Identificación</label>
                    <input type="text" name="identificacion" class="form-input"
                           placeholder="Ingresa tu ID o cédula" required autofocus
                           value="<%= request.getAttribute("identificacion") != null ? request.getAttribute("identificacion") : "" %>">
                </div>

                <div class="form-grupo">
                    <label class="form-label">Contraseña</label>
                    <input type="password" name="contrasena" class="form-input"
                           placeholder="Ingresa tu contraseña" required>
                </div>

                <div class="form-grupo">
                    <label class="form-label">Selecciona tu rol</label>
                    <div class="role-selector">
                        <div class="role-option">
                            <input type="radio" id="operario" name="rol" value="operario" required>
                            <label for="operario" class="role-label">
                                <div class="role-icon"><i class="fas fa-hard-hat"></i></div>
                                <div>Operario</div>
                            </label>
                        </div>
                        <div class="role-option">
                            <input type="radio" id="admin" name="rol" value="admin">
                            <label for="admin" class="role-label">
                                <div class="role-icon"><i class="fas fa-user-tie"></i></div>
                                <div>Administrador</div>
                            </label>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-principal w-100">INICIAR SESIÓN</button>

            </form>

            <p class="text-center mt-3" style="font-size: 12px; color: #64748b;">
                Sistema de Gestión de Producción — Makairos SAS
            </p>
        </div>
    </div>

</body>
</html>
