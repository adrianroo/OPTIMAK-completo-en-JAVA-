<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="co.optimak.modelo.EmpleadoOperativo" %>
<%@ page import="co.optimak.modelo.ModuloOperativo" %>
<%@ page import="co.optimak.modelo.JornadaModulo" %>
<%@ page import="co.optimak.modelo.PausaModulo" %>
<%
    String tabActivo  = (String) request.getAttribute("tabActivo");
    if (tabActivo == null) tabActivo = "empleados";
    String mensaje    = (String) request.getAttribute("mensaje");
    String sesionNombre = (String) session.getAttribute("sesionNombre");

    List<EmpleadoOperativo>  listaEmpleados = (List<EmpleadoOperativo>)  request.getAttribute("listaEmpleados");
    List<ModuloOperativo>    listaModulos   = (List<ModuloOperativo>)    request.getAttribute("listaModulos");

    // datos del tab horarios
    String moduloHorarioSeleccionado = (String) request.getAttribute("moduloHorarioSeleccionado");
    String fechaHorarioSeleccionada  = (String) request.getAttribute("fechaHorarioSeleccionada");
    JornadaModulo       jornadaActual    = (JornadaModulo) request.getAttribute("jornadaActual");
    List<PausaModulo>   pausasActuales   = (List<PausaModulo>) request.getAttribute("pausasActuales");
    List<JornadaModulo> jornadasDelModulo = (List<JornadaModulo>) request.getAttribute("jornadasDelModulo");

    boolean mostrarConfigHorario = moduloHorarioSeleccionado != null
            && !moduloHorarioSeleccionado.isEmpty()
            && fechaHorarioSeleccionada != null
            && !fechaHorarioSeleccionada.isEmpty();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Gestión de Personal</title>
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
            <a href="personal" class="active">Personal</a>
            <a href="informes">Informes</a>
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
            <h2 class="titulo-seccion">Gestión de Personal</h2>
            <p class="subtitulo-seccion">Módulo Administrativo</p>

            <% if (mensaje != null && !mensaje.isEmpty()) { %>
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <%= mensaje %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <%-- TABS --%>
            <div class="tabs-contenedor">
                <button class="tab <%= "empleados".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('empleados', this)">
                    Gestión de Usuarios
                </button>
                <button class="tab <%= "modulos".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('modulos', this)">
                    Módulos Operativos
                </button>
                <button class="tab <%= "horarios".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('horarios', this)">
                    Bloques de Horario
                </button>
            </div>

            <%-- ============================================================
                 TAB: EMPLEADOS
            ============================================================ --%>
            <div id="tab-empleados" class="tab-content"
                 style="<%= "empleados".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="action-bar">
                    <div></div>
                    <button class="btn-principal" onclick="abrirModal('modalCrearEmpleado')">
                        <i class="fas fa-plus"></i> Crear Usuario
                    </button>
                </div>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Cédula</th>
                                <th>Teléfono</th>
                                <th>Edad</th>
                                <th>Sexo</th>
                                <th>Cargo</th>
                                <th>Correo</th>
                                <th>Módulo</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaEmpleados == null || listaEmpleados.isEmpty()) { %>
                                <tr>
                                    <td colspan="9" class="text-center" style="padding:40px; color:#64748b;">
                                        No hay operarios activos registrados
                                    </td>
                                </tr>
                            <% } else { for (EmpleadoOperativo emp : listaEmpleados) { %>
                                <tr>
                                    <td><strong><%= emp.getNombre() %></strong></td>
                                    <td><%= emp.getNumeroCedula() %></td>
                                    <td><%= emp.getTelefono() != null ? emp.getTelefono() : "—" %></td>
                                    <td><%= emp.getEdad() %></td>
                                    <td><%= emp.getSexo() %></td>
                                    <td><%= emp.getCargoRol() %></td>
                                    <td><%= emp.getCorreo() != null ? emp.getCorreo() : "—" %></td>
                                    <td>Módulo <%= emp.getModuloOperativoId() %></td>
                                    <td>
                                        <%-- Editar datos básicos --%>
                                        <button class="btn-icono" title="Editar datos"
                                                data-cedula="<%= emp.getNumeroCedula() %>"
                                                data-nombre="<%= emp.getNombre().replace("\"", "&quot;") %>"
                                                data-telefono="<%= emp.getTelefono() != null ? emp.getTelefono() : "" %>"
                                                data-edad="<%= emp.getEdad() %>"
                                                data-sexo="<%= emp.getSexo() %>"
                                                data-cargo="<%= emp.getCargoRol().replace("\"", "&quot;") %>"
                                                data-correo="<%= emp.getCorreo() != null ? emp.getCorreo() : "" %>"
                                                data-modulo="<%= emp.getModuloOperativoId() %>"
                                                onclick="editarEmpleado(this)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <%-- Desactivar: no elimina, solo pone Activo=0 --%>
                                        <form method="post" action="personal" style="display:inline;"
                                              onsubmit="return confirm('¿Desactivar al operario <%= emp.getNombre() %>? Sus registros históricos se conservarán.');">
                                            <input type="hidden" name="entidad" value="empleado">
                                            <input type="hidden" name="accion"  value="desactivar">
                                            <input type="hidden" name="cedula"  value="<%= emp.getNumeroCedula() %>">
                                            <button type="submit" class="btn-icono" title="Desactivar operario">
                                                <i class="fas fa-user-slash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div><%-- fin tab-empleados --%>


            <%-- ============================================================
                 TAB: MÓDULOS
            ============================================================ --%>
            <div id="tab-modulos" class="tab-content"
                 style="<%= "modulos".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="action-bar">
                    <div></div>
                    <button class="btn-principal" onclick="abrirModal('modalCrearModulo')">
                        <i class="fas fa-plus"></i> Crear Módulo
                    </button>
                </div>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>ID Módulo</th>
                                <th>N° Operarios Activos</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaModulos == null || listaModulos.isEmpty()) { %>
                                <tr>
                                    <td colspan="3" class="text-center" style="padding:40px; color:#64748b;">
                                        No hay módulos operativos registrados
                                    </td>
                                </tr>
                            <% } else { for (ModuloOperativo mod : listaModulos) { %>
                                <tr>
                                    <td><strong>Módulo <%= mod.getIdModulo() %></strong></td>
                                    <td><%= mod.getCantOperarios() %></td>
                                    <td>
                                        <form method="post" action="personal" style="display:inline;"
                                              onsubmit="return confirm('¿Eliminar el módulo <%= mod.getIdModulo() %>? Solo es posible si no tiene empleados ni lotes asignados.');">
                                            <input type="hidden" name="entidad"  value="modulo">
                                            <input type="hidden" name="accion"   value="eliminar">
                                            <input type="hidden" name="idModulo" value="<%= mod.getIdModulo() %>">
                                            <button type="submit" class="btn-icono" title="Eliminar módulo">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div><%-- fin tab-modulos --%>


            <%-- ============================================================
                 TAB: BLOQUES DE HORARIO
            ============================================================ --%>
            <div id="tab-horarios" class="tab-content"
                 style="<%= "horarios".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="info-box mb-4">
                    <p><strong>Configuración de Bloques de Horario por Módulo</strong></p>
                    <p style="margin-bottom:0;">Cada fecha puede tener un horario único. El sistema excluye
                    automáticamente los tiempos de pausa para calcular solo el tiempo productivo.</p>
                </div>

                <%-- Formulario GET para seleccionar módulo + fecha --%>
                <form method="get" action="personal">
                    <input type="hidden" name="tab" value="horarios">
                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">Seleccionar Módulo</label>
                            <select name="moduloId" class="form-input" required>
                                <option value="">Seleccione un módulo...</option>
                                <% if (listaModulos != null) { for (ModuloOperativo mod : listaModulos) {
                                    boolean selMod = mod.getIdModulo().equals(moduloHorarioSeleccionado);
                                %>
                                    <option value="<%= mod.getIdModulo() %>" <%= selMod ? "selected" : "" %>>
                                        Módulo <%= mod.getIdModulo() %>
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Fecha a Configurar</label>
                            <input type="date" name="fecha" class="form-input"
                                   value="<%= fechaHorarioSeleccionada != null ? fechaHorarioSeleccionada : "" %>" required>
                        </div>
                        <div class="form-grupo" style="display:flex; align-items:flex-end;">
                            <button type="submit" class="btn-principal" style="width:100%;">
                                <i class="fas fa-search"></i> Consultar / Configurar
                            </button>
                        </div>
                    </div>
                </form>

                <% if (mostrarConfigHorario) {
                    boolean jornadaExistente = jornadaActual != null;
                %>

                <% if (jornadaExistente) { %>
                    <div class="alerta-info" style="margin-top:16px;">
                        <i class="fas fa-info-circle" style="margin-right:8px;"></i>
                        Esta fecha ya tiene una configuración guardada. Puedes editarla aquí.
                    </div>
                <% } %>

                <%-- Formulario POST para guardar la jornada y sus pausas --%>
                <div style="margin-top:30px;">
                    <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; margin-bottom:20px;">
                        <div>
                            <h4 style="font-size:18px; font-weight:700; margin-bottom:4px;">
                                Configurando: <span style="color:#2563eb;">
                                    Módulo <%= moduloHorarioSeleccionado %> /
                                    <%= fechaHorarioSeleccionada %>
                                </span>
                            </h4>
                            <p style="font-size:13px; color:#64748b; margin:0;">
                                Define la jornada y las pausas para esta fecha específica.
                            </p>
                        </div>
                    </div>

                    <form method="post" action="personal">
                        <input type="hidden" name="entidad"  value="jornada">
                        <input type="hidden" name="accion"   value="guardar">
                        <input type="hidden" name="moduloId" value="<%= moduloHorarioSeleccionado %>">
                        <input type="hidden" name="fecha"    value="<%= fechaHorarioSeleccionada %>">

                        <div class="tarjeta" style="background:#f5f7fa; padding:20px;">
                            <h5 style="font-size:16px; font-weight:700; margin-bottom:15px;">Jornada Laboral</h5>

                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label class="form-label">Hora Inicio Jornada *</label>
                                    <input type="time" name="horaInicio" class="form-input" required
                                           value="<%= jornadaExistente ? jornadaActual.getHoraInicio() : "06:00" %>">
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Hora Fin Jornada *</label>
                                    <input type="time" name="horaFin" class="form-input" required
                                           value="<%= jornadaExistente ? jornadaActual.getHoraFin() : "14:20" %>">
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Día No Laborable</label>
                                    <div style="padding:8px 0;">
                                        <label style="display:flex; align-items:center; gap:10px; cursor:pointer;">
                                            <input type="checkbox" name="diaNoLaborable"
                                                   style="width:18px; height:18px;"
                                                   <%= (jornadaExistente && jornadaActual.getEsDiaLaborable() == 0) ? "checked" : "" %>>
                                            <span>Marcar como festivo / descanso</span>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <h5 style="font-size:16px; font-weight:700; margin-bottom:15px; margin-top:20px;">
                                Pausas y Recesos
                            </h5>

                            <div id="listaPausas">
                                <%-- Si hay pausas guardadas las precargamos; si no, una fila vacía de ejemplo --%>
                                <% if (pausasActuales != null && !pausasActuales.isEmpty()) {
                                    for (PausaModulo p : pausasActuales) { %>
                                    <div class="row g-3 mb-3 align-items-end fila-pausa"
                                         style="background:white; padding:15px; border-radius:8px;">
                                        <div class="col-md-4">
                                            <label class="form-label">Tipo de Pausa</label>
                                            <select name="pausaTipo[]" class="form-input">
                                                <option value="Desayuno"    <%= "Desayuno".equals(p.getTipoPausa())     ? "selected" : "" %>>Desayuno</option>
                                                <option value="Almuerzo"    <%= "Almuerzo".equals(p.getTipoPausa())     ? "selected" : "" %>>Almuerzo</option>
                                                <option value="Pausa Activa" <%= "Pausa Activa".equals(p.getTipoPausa()) ? "selected" : "" %>>Pausa Activa</option>
                                                <option value="Otro"        <%= "Otro".equals(p.getTipoPausa())         ? "selected" : "" %>>Otro</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Hora Inicio</label>
                                            <input type="time" name="pausaHora[]" class="form-input"
                                                   value="<%= p.getHoraInicioPausa() %>">
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Duración (min)</label>
                                            <input type="number" name="pausaDuracion[]" class="form-input"
                                                   value="<%= p.getDuracionMinutos() %>" min="1">
                                        </div>
                                        <div class="col-md-2">
                                            <button type="button" class="btn-icono" title="Eliminar pausa"
                                                    onclick="eliminarFilaPausa(this)">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                <% } } else { %>
                                    <%-- fila vacía para que el admin agregue la primera pausa --%>
                                    <div class="row g-3 mb-3 align-items-end fila-pausa"
                                         style="background:white; padding:15px; border-radius:8px;">
                                        <div class="col-md-4">
                                            <label class="form-label">Tipo de Pausa</label>
                                            <select name="pausaTipo[]" class="form-input">
                                                <option value="Desayuno">Desayuno</option>
                                                <option value="Almuerzo">Almuerzo</option>
                                                <option value="Pausa Activa">Pausa Activa</option>
                                                <option value="Otro">Otro</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Hora Inicio</label>
                                            <input type="time" name="pausaHora[]" class="form-input" value="09:15">
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Duración (min)</label>
                                            <input type="number" name="pausaDuracion[]" class="form-input" value="20" min="1">
                                        </div>
                                        <div class="col-md-2">
                                            <button type="button" class="btn-icono" title="Eliminar pausa"
                                                    onclick="eliminarFilaPausa(this)">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                <% } %>
                            </div>

                            <button type="button" class="btn-secundario mt-3" onclick="agregarFilaPausa()">
                                <i class="fas fa-plus"></i> Agregar Pausa
                            </button>

                            <div class="text-end mt-4">
                                <a href="personal?tab=horarios" class="btn-secundario me-2">Cancelar</a>
                                <button type="submit" class="btn-principal">
                                    <i class="fas fa-save"></i>
                                    <%= jornadaExistente ? "Actualizar Configuración" : "Guardar Configuración" %>
                                </button>
                            </div>
                        </div>
                    </form>

                    <%-- Historial de fechas configuradas para este módulo --%>
                    <% if (jornadasDelModulo != null && !jornadasDelModulo.isEmpty()) { %>
                    <div class="tarjeta" style="margin-top:20px;">
                        <h5 style="font-size:16px; font-weight:700; margin-bottom:16px;">
                            <i class="fas fa-calendar-alt" style="color:#2563eb; margin-right:8px;"></i>
                            Fechas configuradas — Módulo <%= moduloHorarioSeleccionado %>
                        </h5>
                        <div class="tabla-contenedor">
                            <table class="tabla">
                                <thead>
                                    <tr>
                                        <th>Fecha</th>
                                        <th>Hora Inicio</th>
                                        <th>Hora Fin</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (JornadaModulo j : jornadasDelModulo) { %>
                                        <tr>
                                            <td><%= j.getFecha() %></td>
                                            <td><%= j.getHoraInicio() %></td>
                                            <td><%= j.getHoraFin() %></td>
                                            <td>
                                                <% if (j.getEsDiaLaborable() == 0) { %>
                                                    <span class="badge-estado badge-pendiente">No laborable</span>
                                                <% } else { %>
                                                    <span class="badge-estado badge-en-proceso">Laborable</span>
                                                <% } %>
                                            </td>
                                            <td>
                                                <%-- Ir a editar esa fecha --%>
                                                <a href="personal?tab=horarios&moduloId=<%= moduloHorarioSeleccionado %>&fecha=<%= j.getFecha() %>"
                                                   class="btn-icono" title="Editar esta fecha">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <%-- Eliminar jornada (también borra pausas por CASCADE) --%>
                                                <form method="post" action="personal" style="display:inline;"
                                                      onsubmit="return confirm('¿Eliminar la configuración del <%= j.getFecha() %>?');">
                                                    <input type="hidden" name="entidad"   value="jornada">
                                                    <input type="hidden" name="accion"    value="eliminar">
                                                    <input type="hidden" name="idJornada" value="<%= j.getIdJornada() %>">
                                                    <button type="submit" class="btn-icono" title="Eliminar">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } %>

                <% } /* fin mostrarConfigHorario */ %>

            </div><%-- fin tab-horarios --%>

        </div><%-- fin tarjeta --%>
    </main>


    <%-- ==================================================================
         MODAL: CREAR EMPLEADO
    ================================================================== --%>
    <div id="modalCrearEmpleado" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Crear Nuevo Operario</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalCrearEmpleado')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="personal">
                    <input type="hidden" name="entidad" value="empleado">
                    <input type="hidden" name="accion"  value="insertar">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">Número de Cédula *</label>
                            <input type="text" name="cedula" class="form-input" placeholder="1000000001" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Nombre Completo *</label>
                            <input type="text" name="nombre" class="form-input" placeholder="Nombre completo" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Teléfono</label>
                            <input type="tel" name="telefono" class="form-input" placeholder="3001234567">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Edad *</label>
                            <input type="number" name="edad" class="form-input" placeholder="25" min="16" max="99" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Sexo *</label>
                            <select name="sexo" class="form-input" required>
                                <option value="">Seleccione...</option>
                                <option value="M">Masculino</option>
                                <option value="F">Femenino</option>
                                <option value="Otro">Otro</option>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Cargo / Rol *</label>
                            <input type="text" name="cargoRol" class="form-input" value="Operario" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Correo Electrónico</label>
                            <input type="email" name="correo" class="form-input" placeholder="correo@ejemplo.com">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Módulo Asignado *</label>
                            <select name="moduloOperativoId" class="form-input" required>
                                <option value="">Seleccione un módulo...</option>
                                <% if (listaModulos != null) { for (ModuloOperativo mod : listaModulos) { %>
                                    <option value="<%= mod.getIdModulo() %>">Módulo <%= mod.getIdModulo() %></option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Contraseña Inicial *</label>
                            <input type="password" name="contrasena" class="form-input" placeholder="Contraseña" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalCrearEmpleado')">Cancelar</button>
                        <button type="submit" class="btn-principal">Crear Operario</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- ==================================================================
         MODAL: EDITAR DATOS BÁSICOS DEL EMPLEADO
    ================================================================== --%>
    <div id="modalEditarEmpleado" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Editar Datos del Operario</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalEditarEmpleado')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="personal">
                    <input type="hidden" name="entidad" value="empleado">
                    <input type="hidden" name="accion"  value="actualizarDatos">
                    <input type="hidden" id="ee-cedula-hidden" name="cedula">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">Cédula</label>
                            <input type="text" id="ee-cedula-display" class="form-input" disabled
                                   style="background:#f1f5f9; color:#64748b;">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Nombre Completo *</label>
                            <input type="text" id="ee-nombre" name="nombre" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Teléfono</label>
                            <input type="tel" id="ee-telefono" name="telefono" class="form-input">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Edad *</label>
                            <input type="number" id="ee-edad" name="edad" class="form-input" min="16" max="99" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Sexo *</label>
                            <select id="ee-sexo" name="sexo" class="form-input" required>
                                <option value="M">Masculino</option>
                                <option value="F">Femenino</option>
                                <option value="Otro">Otro</option>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Cargo / Rol *</label>
                            <input type="text" id="ee-cargo" name="cargoRol" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Correo Electrónico</label>
                            <input type="email" id="ee-correo" name="correo" class="form-input">
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalEditarEmpleado')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Cambios</button>
                    </div>
                </form>

                <%-- Sección para cambio de módulo — acción separada con su propia transacción --%>
                <hr style="margin:20px 0;">
                <h5 style="font-size:14px; font-weight:600; margin-bottom:12px;">Cambiar Módulo Asignado</h5>
                <form method="post" action="personal"
                      onsubmit="return confirm('¿Confirmar cambio de módulo? Se registrará en el historial.');">
                    <input type="hidden" name="entidad" value="empleado">
                    <input type="hidden" name="accion"  value="cambiarModulo">
                    <input type="hidden" id="cm-cedula" name="cedula">

                    <div class="row g-2 align-items-end">
                        <div class="col">
                            <label class="form-label">Nuevo Módulo</label>
                            <select id="cm-modulo" name="nuevoModuloId" class="form-input" required>
                                <option value="">Seleccione...</option>
                                <% if (listaModulos != null) { for (ModuloOperativo mod : listaModulos) { %>
                                    <option value="<%= mod.getIdModulo() %>">Módulo <%= mod.getIdModulo() %></option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="col-auto">
                            <button type="submit" class="btn-principal">
                                <i class="fas fa-exchange-alt"></i> Cambiar
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- ==================================================================
         MODAL: CREAR MÓDULO
    ================================================================== --%>
    <div id="modalCrearModulo" class="modal-overlay">
        <div class="modal-contenido" style="max-width:480px;">
            <div class="modal-header">
                <h2>Crear Nuevo Módulo Operativo</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalCrearModulo')">&times;</button>
            </div>
            <div class="modal-body">
                <div class="info-box">
                    <p><strong>Nota:</strong> El ID del módulo es un código de hasta 2 caracteres (ej: 1, 2, A, B).</p>
                </div>
                <form method="post" action="personal">
                    <input type="hidden" name="entidad" value="modulo">
                    <input type="hidden" name="accion"  value="insertar">

                    <div class="form-grupo">
                        <label class="form-label">ID Módulo *</label>
                        <input type="text" name="idModulo" class="form-input"
                               placeholder="Ej: 1, 2, 3..." maxlength="2" required>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalCrearModulo')">Cancelar</button>
                        <button type="submit" class="btn-principal">Crear Módulo</button>
                    </div>
                </form>
            </div>
        </div>
    </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // ---------------------------------------------------------------
        // Tabs
        // ---------------------------------------------------------------
        function cambiarTab(nombre, boton) {
            document.querySelectorAll('.tab-content').forEach(function(t) { t.style.display = 'none'; });
            document.querySelectorAll('.tab').forEach(function(t) { t.classList.remove('active'); });
            document.getElementById('tab-' + nombre).style.display = 'block';
            boton.classList.add('active');
        }

        // ---------------------------------------------------------------
        // Modales
        // ---------------------------------------------------------------
        function abrirModal(id) { document.getElementById(id).classList.add('show'); }
        function cerrarModal(id) { document.getElementById(id).classList.remove('show'); }
        window.onclick = function(event) {
            if (event.target.classList.contains('modal-overlay')) {
                event.target.classList.remove('show');
            }
        };

        // ---------------------------------------------------------------
        // Empleados — data attributes para evitar romper JS con tildes
        // ---------------------------------------------------------------
        function editarEmpleado(boton) {
            document.getElementById('ee-cedula-hidden').value  = boton.dataset.cedula;
            document.getElementById('ee-cedula-display').value = boton.dataset.cedula;
            document.getElementById('ee-nombre').value    = boton.dataset.nombre;
            document.getElementById('ee-telefono').value  = boton.dataset.telefono || '';
            document.getElementById('ee-edad').value      = boton.dataset.edad;
            document.getElementById('ee-sexo').value      = boton.dataset.sexo;
            document.getElementById('ee-cargo').value     = boton.dataset.cargo;
            document.getElementById('ee-correo').value    = boton.dataset.correo || '';
            // también carga la cédula en el form de cambio de módulo
            document.getElementById('cm-cedula').value    = boton.dataset.cedula;
            // pre-selecciona el módulo actual en el select de cambio
            document.getElementById('cm-modulo').value    = boton.dataset.modulo;
            abrirModal('modalEditarEmpleado');
        }

        // ---------------------------------------------------------------
        // Pausas — agregar y eliminar filas dinámicamente
        // ---------------------------------------------------------------
        function agregarFilaPausa() {
            var fila = document.createElement('div');
            fila.className = 'row g-3 mb-3 align-items-end fila-pausa';
            fila.style.cssText = 'background:white; padding:15px; border-radius:8px;';
            fila.innerHTML = `
                <div class="col-md-4">
                    <label class="form-label">Tipo de Pausa</label>
                    <select name="pausaTipo[]" class="form-input">
                        <option value="Desayuno">Desayuno</option>
                        <option value="Almuerzo">Almuerzo</option>
                        <option value="Pausa Activa">Pausa Activa</option>
                        <option value="Otro">Otro</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Hora Inicio</label>
                    <input type="time" name="pausaHora[]" class="form-input">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Duración (min)</label>
                    <input type="number" name="pausaDuracion[]" class="form-input" value="20" min="1">
                </div>
                <div class="col-md-2">
                    <button type="button" class="btn-icono" title="Eliminar pausa"
                            onclick="eliminarFilaPausa(this)">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>`;
            document.getElementById('listaPausas').appendChild(fila);
        }

        function eliminarFilaPausa(boton) {
            var fila = boton.closest('.fila-pausa');
            if (fila) fila.remove();
        }
    </script>
</body>
</html>
