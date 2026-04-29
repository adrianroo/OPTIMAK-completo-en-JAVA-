<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="co.optimak.modelo.Lote" %>
<%@ page import="co.optimak.modelo.Referencia" %>
<%@ page import="co.optimak.modelo.Operacion" %>
<%@ page import="co.optimak.modelo.ModuloOperativo" %>
<%
    String tabActivo = (String) request.getAttribute("tabActivo");
    if (tabActivo == null) tabActivo = "lotes";
    String mensaje = (String) request.getAttribute("mensaje");

    List<Lote>           listaLotes       = (List<Lote>)           request.getAttribute("listaLotes");
    List<Referencia>     listaReferencias = (List<Referencia>)     request.getAttribute("listaReferencias");
    List<Operacion>      listaOperaciones = (List<Operacion>)      request.getAttribute("listaOperaciones");
    List<ModuloOperativo> listaModulos    = (List<ModuloOperativo>) request.getAttribute("listaModulos");

    String sesionNombre = (String) session.getAttribute("sesionNombre");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OPTIMAK - Gestión de Producción</title>
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
            <a href="produccion" class="active">Producción</a>
            <a href="personal">Personal</a>
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
            <h2 class="titulo-seccion">Gestión de Producción</h2>
            <p class="subtitulo-seccion">Módulo Administrativo</p>

            <%-- Mensaje de éxito o error después de una acción --%>
            <% if (mensaje != null && !mensaje.isEmpty()) { %>
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <%= mensaje %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <%-- TABS --%>
            <div class="tabs-contenedor">
                <button class="tab <%= "lotes".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('lotes', this)">
                    Lotes de Producción
                </button>
                <button class="tab <%= "referencias".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('referencias', this)">
                    Referencias
                </button>
                <button class="tab <%= "operaciones".equals(tabActivo) ? "active" : "" %>"
                        onclick="cambiarTab('operaciones', this)">
                    Operaciones
                </button>
            </div>

            <%-- ============================================================
                 TAB: LOTES
            ============================================================ --%>
            <div id="tab-lotes" class="tab-content"
                 style="<%= "lotes".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="action-bar">
                    <div></div>
                    <button class="btn-principal" onclick="abrirModal('modalCrearLote')">
                        <i class="fas fa-plus"></i> Crear Lote
                    </button>
                </div>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>OM</th>
                                <th>Referencia</th>
                                <th>Remisión</th>
                                <th>Color</th>
                                <th>Total Unidades</th>
                                <th>Módulo</th>
                                <th>Fecha Ingreso</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaLotes == null || listaLotes.isEmpty()) { %>
                                <tr>
                                    <td colspan="8" class="text-center" style="padding:40px; color:#64748b;">
                                        No hay lotes registrados
                                    </td>
                                </tr>
                            <% } else { for (Lote lote : listaLotes) { %>
                                <tr>
                                    <td><strong><%= lote.getOm() %></strong></td>
                                    <td><%= lote.getReferenciaIdRef() %></td>
                                    <td><%= lote.getNumeroRemisionEntrada() %></td>
                                    <td><%= lote.getColor() %></td>
                                    <td><%= lote.getTotalUnidades() %></td>
                                    <td>Módulo <%= lote.getModuloOperativoId() %></td>
                                    <td><%= lote.getFechaIngresoPlanita() %></td>
                                    <td>
                                        <button class="btn-icono" title="Ver detalle"
                                                onclick="verDetalleLote(
                                                    '<%= lote.getOm() %>',
                                                    '<%= lote.getReferenciaIdRef() %>',
                                                    '<%= lote.getColor() %>',
                                                    '<%= lote.getNumeroRemisionEntrada() %>',
                                                    '<%= lote.getOc() != null ? lote.getOc() : "" %>',
                                                    '<%= lote.getFechaIngresoPlanita() %>',
                                                    '<%= lote.getModuloOperativoId() %>',
                                                    '<%= lote.getCantXXL() %>',
                                                    '<%= lote.getCantXL() %>',
                                                    '<%= lote.getCantL() %>',
                                                    '<%= lote.getCantM() %>',
                                                    '<%= lote.getCantS() %>',
                                                    '<%= lote.getCantXS() %>'
                                                )">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button class="btn-icono" title="Editar"
                                                onclick="editarLote(
                                                    '<%= lote.getOm() %>',
                                                    '<%= lote.getReferenciaIdRef() %>',
                                                    '<%= lote.getColor() %>',
                                                    '<%= lote.getNumeroRemisionEntrada() %>',
                                                    '<%= lote.getOc() != null ? lote.getOc() : "" %>',
                                                    '<%= lote.getFechaIngresoPlanita() %>',
                                                    '<%= lote.getModuloOperativoId() %>',
                                                    '<%= lote.getCantXXL() %>',
                                                    '<%= lote.getCantXL() %>',
                                                    '<%= lote.getCantL() %>',
                                                    '<%= lote.getCantM() %>',
                                                    '<%= lote.getCantS() %>',
                                                    '<%= lote.getCantXS() %>'
                                                )">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <form method="post" action="produccion" style="display:inline;"
                                              onsubmit="return confirm('¿Eliminar el lote <%= lote.getOm() %>?');">
                                            <input type="hidden" name="entidad" value="lote">
                                            <input type="hidden" name="accion"  value="eliminar">
                                            <input type="hidden" name="om"      value="<%= lote.getOm() %>">
                                            <button type="submit" class="btn-icono" title="Eliminar">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div><%-- fin tab-lotes --%>

            <%-- ============================================================
                 TAB: REFERENCIAS
            ============================================================ --%>
            <div id="tab-referencias" class="tab-content"
                 style="<%= "referencias".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="action-bar">
                    <div></div>
                    <button class="btn-principal" onclick="abrirModal('modalCrearReferencia')">
                        <i class="fas fa-plus"></i> Crear Referencia
                    </button>
                </div>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>ID Referencia</th>
                                <th>Tipo de Prenda</th>
                                <th>Cliente</th>
                                <th>Colección</th>
                                <th>SAM Total</th>
                                <th>Precio Unitario</th>
                                <th>N° Operaciones</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaReferencias == null || listaReferencias.isEmpty()) { %>
                                <tr>
                                    <td colspan="8" class="text-center" style="padding:40px; color:#64748b;">
                                        No hay referencias registradas
                                    </td>
                                </tr>
                            <% } else { for (Referencia ref : listaReferencias) { %>
                                <tr>
                                    <td><strong><%= ref.getIdRef() %></strong></td>
                                    <td><%= ref.getTipoPrenda() %></td>
                                    <td><%= ref.getCliente() %></td>
                                    <td><%= ref.getColeccion() != null ? ref.getColeccion() : "—" %></td>
                                    <td><%= String.format("%.2f", ref.getTotalSam()) %></td>
                                    <td>$<%= String.format("%.2f", ref.getPrecioUnitario()) %></td>
                                    <td><%= ref.getCantOperaciones() %></td>
                                    <td>
                                        <button class="btn-icono" title="Editar"
                                                data-id="<%= ref.getIdRef() %>"
                                                data-tipo="<%= ref.getTipoPrenda() %>"
                                                data-cliente="<%= ref.getCliente() %>"
                                                data-coleccion="<%= ref.getColeccion() != null ? ref.getColeccion() : "" %>"
                                                data-precio="<%= ref.getPrecioUnitario() %>"
                                                onclick="editarReferencia(this)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <form method="post" action="produccion" style="display:inline;"
                                              onsubmit="return confirm('¿Eliminar la referencia <%= ref.getIdRef() %>? Solo es posible si no tiene lotes ni operaciones.');">
                                            <input type="hidden" name="entidad" value="referencia">
                                            <input type="hidden" name="accion"  value="eliminar">
                                            <input type="hidden" name="idRef"   value="<%= ref.getIdRef() %>">
                                            <button type="submit" class="btn-icono" title="Eliminar">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div><%-- fin tab-referencias --%>

            <%-- ============================================================
                 TAB: OPERACIONES
            ============================================================ --%>
            <div id="tab-operaciones" class="tab-content"
                 style="<%= "operaciones".equals(tabActivo) ? "display:block" : "display:none" %>">

                <div class="action-bar">
                    <div></div>
                    <button class="btn-principal" onclick="abrirModal('modalCrearOperacion')">
                        <i class="fas fa-plus"></i> Crear Operación
                    </button>
                </div>

                <div class="tabla-contenedor">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Referencia</th>
                                <th>Nombre Operación</th>
                                <th>Detalle</th>
                                <th>Máquina</th>
                                <th>SAM</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaOperaciones == null || listaOperaciones.isEmpty()) { %>
                                <tr>
                                    <td colspan="7" class="text-center" style="padding:40px; color:#64748b;">
                                        No hay operaciones registradas
                                    </td>
                                </tr>
                            <% } else { for (Operacion op : listaOperaciones) {
                                   String detalleCorto = op.getDetalle() != null && op.getDetalle().length() > 55
                                           ? op.getDetalle().substring(0, 55) + "..."
                                           : op.getDetalle();
                            %>
                                <tr>
                                    <td><%= op.getIdOperacion() %></td>
                                    <td><%= op.getReferenciaIdRef() %></td>
                                    <td><%= op.getNombreCorto() %></td>
                                    <td title="<%= op.getDetalle() %>"><%= detalleCorto %></td>
                                    <td><%= op.getMaquina() %></td>
                                    <td><%= String.format("%.2f", op.getSamOperacion()) %></td>
                                    <td>
                                        <%-- data attributes para evitar romper el JS con caracteres especiales --%>
                                        <button class="btn-icono" title="Editar"
                                                data-id="<%= op.getIdOperacion() %>"
                                                data-ref="<%= op.getReferenciaIdRef() %>"
                                                data-nombre="<%= op.getNombreCorto() %>"
                                                data-maquina="<%= op.getMaquina() %>"
                                                data-sam="<%= op.getSamOperacion() %>"
                                                data-detalle="<%= op.getDetalle() != null ? op.getDetalle().replace("&", "&amp;").replace("\"", "&quot;") : "" %>"
                                                onclick="editarOperacion(this)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icono btn-icono-detalle" title="Ver detalle completo"
                                                data-detalle="<%= op.getDetalle() != null ? op.getDetalle().replace("\"", "&quot;") : "" %>"
                                                onclick="verDetalleOperacion(this)">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <form method="post" action="produccion" style="display:inline;"
                                              onsubmit="return confirm('¿Eliminar la operación <%= op.getIdOperacion() %>?');">
                                            <input type="hidden" name="entidad"      value="operacion">
                                            <input type="hidden" name="accion"       value="eliminar">
                                            <input type="hidden" name="idOperacion"  value="<%= op.getIdOperacion() %>">
                                            <button type="submit" class="btn-icono" title="Eliminar">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div><%-- fin tab-operaciones --%>

        </div><%-- fin tarjeta --%>
    </main>


    <%-- ==================================================================
         MODALES — LOTES
    ================================================================== --%>

    <%-- Ver detalle de un lote --%>
    <div id="modalVerLote" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Detalle del Lote</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalVerLote')">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-grid">
                    <div class="form-grupo">
                        <label class="form-label">OM</label>
                        <p id="vl-om" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">Referencia</label>
                        <p id="vl-ref" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">Color</label>
                        <p id="vl-color" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">N° Remisión</label>
                        <p id="vl-remision" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">OC</label>
                        <p id="vl-oc" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">Fecha Ingreso</label>
                        <p id="vl-fecha" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                    <div class="form-grupo">
                        <label class="form-label">Módulo</label>
                        <p id="vl-modulo" class="form-input" style="background:#f8fafc;"></p>
                    </div>
                </div>
                <hr>
                <p class="form-label mb-2">Cantidades por talla:</p>
                <div class="row text-center">
                    <div class="col"><small>XXL</small><br><strong id="vl-xxl"></strong></div>
                    <div class="col"><small>XL</small><br><strong id="vl-xl"></strong></div>
                    <div class="col"><small>L</small><br><strong id="vl-l"></strong></div>
                    <div class="col"><small>M</small><br><strong id="vl-m"></strong></div>
                    <div class="col"><small>S</small><br><strong id="vl-s"></strong></div>
                    <div class="col"><small>XS</small><br><strong id="vl-xs"></strong></div>
                </div>
            </div>
        </div>
    </div>

    <%-- Crear lote --%>
    <div id="modalCrearLote" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Crear Nuevo Lote</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalCrearLote')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad" value="lote">
                    <input type="hidden" name="accion"  value="insertar">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">OM (Orden Maestra) *</label>
                            <input type="text" name="om" class="form-input" placeholder="OM-2025-001" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">N° Remisión Entrada *</label>
                            <input type="text" name="numeroRemisionEntrada" class="form-input" placeholder="REM-001" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">OC (Orden de Compra)</label>
                            <input type="text" name="oc" class="form-input" placeholder="Opcional">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Referencia *</label>
                            <select name="referenciaIdRef" class="form-input" required>
                                <option value="">Seleccione una referencia...</option>
                                <% if (listaReferencias != null) { for (Referencia r : listaReferencias) { %>
                                    <option value="<%= r.getIdRef() %>">
                                        <%= r.getIdRef() %> — <%= r.getTipoPrenda() %> (<%= r.getCliente() %>)
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Color *</label>
                            <input type="text" name="color" class="form-input" placeholder="Negro" required>
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
                            <label class="form-label">Fecha Ingreso a Planta *</label>
                            <input type="date" name="fechaIngreso" class="form-input" required>
                        </div>
                    </div>

                    <hr>
                    <p class="form-label mb-2">Cantidades por talla:</p>
                    <div class="row">
                        <div class="col-md-2 form-grupo"><label class="form-label">XXL</label>
                            <input type="number" name="cantXXL" class="form-input" value="0" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">XL</label>
                            <input type="number" name="cantXL"  class="form-input" value="0" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">L</label>
                            <input type="number" name="cantL"   class="form-input" value="0" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">M</label>
                            <input type="number" name="cantM"   class="form-input" value="0" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">S</label>
                            <input type="number" name="cantS"   class="form-input" value="0" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">XS</label>
                            <input type="number" name="cantXS"  class="form-input" value="0" min="0"></div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalCrearLote')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Lote</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- Editar lote --%>
    <div id="modalEditarLote" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Editar Lote</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalEditarLote')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad" value="lote">
                    <input type="hidden" name="accion"  value="actualizar">
                    <input type="hidden" id="el-om-hidden" name="om">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">OM</label>
                            <input type="text" id="el-om-display" class="form-input" disabled
                                   style="background:#f1f5f9; color:#64748b;">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">N° Remisión Entrada *</label>
                            <input type="text" id="el-remision" name="numeroRemisionEntrada" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">OC</label>
                            <input type="text" id="el-oc" name="oc" class="form-input">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Referencia *</label>
                            <select id="el-referencia" name="referenciaIdRef" class="form-input" required>
                                <option value="">Seleccione una referencia...</option>
                                <% if (listaReferencias != null) { for (Referencia r : listaReferencias) { %>
                                    <option value="<%= r.getIdRef() %>">
                                        <%= r.getIdRef() %> — <%= r.getTipoPrenda() %> (<%= r.getCliente() %>)
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Color *</label>
                            <input type="text" id="el-color" name="color" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Módulo Asignado *</label>
                            <select id="el-modulo" name="moduloOperativoId" class="form-input" required>
                                <option value="">Seleccione un módulo...</option>
                                <% if (listaModulos != null) { for (ModuloOperativo mod : listaModulos) { %>
                                    <option value="<%= mod.getIdModulo() %>">Módulo <%= mod.getIdModulo() %></option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Fecha Ingreso a Planta *</label>
                            <input type="date" id="el-fecha" name="fechaIngreso" class="form-input" required>
                        </div>
                    </div>

                    <hr>
                    <p class="form-label mb-2">Cantidades por talla:</p>
                    <div class="row">
                        <div class="col-md-2 form-grupo"><label class="form-label">XXL</label>
                            <input type="number" id="el-xxl" name="cantXXL" class="form-input" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">XL</label>
                            <input type="number" id="el-xl"  name="cantXL"  class="form-input" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">L</label>
                            <input type="number" id="el-l"   name="cantL"   class="form-input" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">M</label>
                            <input type="number" id="el-m"   name="cantM"   class="form-input" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">S</label>
                            <input type="number" id="el-s"   name="cantS"   class="form-input" min="0"></div>
                        <div class="col-md-2 form-grupo"><label class="form-label">XS</label>
                            <input type="number" id="el-xs"  name="cantXS"  class="form-input" min="0"></div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalEditarLote')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Cambios</button>
                    </div>
                </form>
            </div>
        </div>
    </div>


    <%-- ==================================================================
         MODALES — REFERENCIAS
    ================================================================== --%>

    <%-- Crear referencia --%>
    <div id="modalCrearReferencia" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Crear Nueva Referencia</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalCrearReferencia')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad" value="referencia">
                    <input type="hidden" name="accion"  value="insertar">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">ID Referencia *</label>
                            <input type="text" name="idRef" class="form-input" placeholder="REF-29781" maxlength="15" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Tipo de Prenda *</label>
                            <input type="text" name="tipoPrenda" class="form-input" placeholder="Ej: Legging largo dama" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Cliente *</label>
                            <input type="text" name="cliente" class="form-input" placeholder="Almacenes Éxito" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Colección</label>
                            <input type="text" name="coleccion" class="form-input" placeholder="Opcional">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Precio Unitario *</label>
                            <input type="number" name="precioUnitario" class="form-input" placeholder="0.00" step="0.01" min="0.01" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalCrearReferencia')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Referencia</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- Editar referencia --%>
    <div id="modalEditarReferencia" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Editar Referencia</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalEditarReferencia')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad"  value="referencia">
                    <input type="hidden" name="accion"   value="actualizar">
                    <input type="hidden" id="er-id-hidden" name="idRef">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">ID Referencia</label>
                            <input type="text" id="er-id-display" class="form-input" disabled
                                   style="background:#f1f5f9; color:#64748b;">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Tipo de Prenda *</label>
                            <input type="text" id="er-tipo" name="tipoPrenda" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Cliente *</label>
                            <input type="text" id="er-cliente" name="cliente" class="form-input" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Colección</label>
                            <input type="text" id="er-coleccion" name="coleccion" class="form-input">
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Precio Unitario *</label>
                            <input type="number" id="er-precio" name="precioUnitario" class="form-input" step="0.01" min="0.01" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalEditarReferencia')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Cambios</button>
                    </div>
                </form>
            </div>
        </div>
    </div>


    <%-- ==================================================================
         MODALES — OPERACIONES
    ================================================================== --%>

    <%-- Crear operación --%>
    <div id="modalCrearOperacion" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Crear Nueva Operación</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalCrearOperacion')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad" value="operacion">
                    <input type="hidden" name="accion"  value="insertar">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">Referencia Asociada *</label>
                            <select name="referenciaIdRef" class="form-input" required>
                                <option value="">Seleccione una referencia...</option>
                                <% if (listaReferencias != null) { for (Referencia r : listaReferencias) { %>
                                    <option value="<%= r.getIdRef() %>">
                                        <%= r.getIdRef() %> — <%= r.getTipoPrenda() %>
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Nombre Operación *</label>
                            <input type="text" name="nombreCorto" class="form-input" placeholder="Ej: Unir Costados" maxlength="40" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Tipo de Máquina *</label>
                            <select name="maquina" class="form-input" required>
                                <option value="">Seleccione...</option>
                                <option value="Manual">Manual</option>
                                <option value="PLA">Plana (PLA)</option>
                                <option value="FIL">Fileteadora (FIL)</option>
                                <option value="REC">Recubridora (REC)</option>
                                <option value="PLAN">Plana Industrial (PLAN)</option>
                                <option value="DOB">Dobladora (DOB)</option>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">SAM (minutos estándar) *</label>
                            <input type="number" name="samOperacion" class="form-input" placeholder="1.20" step="0.01" min="0.01" required>
                        </div>
                    </div>

                    <div class="form-grupo">
                        <label class="form-label">Detalle de la Operación *</label>
                        <textarea name="detalle" class="form-input" rows="3"
                                  placeholder="Descripción detallada de cómo ejecutar la operación..."
                                  maxlength="255" required></textarea>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalCrearOperacion')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Operación</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- Editar operación --%>
    <div id="modalEditarOperacion" class="modal-overlay">
        <div class="modal-contenido">
            <div class="modal-header">
                <h2>Editar Operación</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalEditarOperacion')">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="produccion">
                    <input type="hidden" name="entidad"     value="operacion">
                    <input type="hidden" name="accion"      value="actualizar">
                    <input type="hidden" id="eo-id" name="idOperacion">

                    <div class="form-grid">
                        <div class="form-grupo">
                            <label class="form-label">Referencia Asociada *</label>
                            <select id="eo-referencia" name="referenciaIdRef" class="form-input" required>
                                <option value="">Seleccione una referencia...</option>
                                <% if (listaReferencias != null) { for (Referencia r : listaReferencias) { %>
                                    <option value="<%= r.getIdRef() %>">
                                        <%= r.getIdRef() %> — <%= r.getTipoPrenda() %>
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Nombre Operación *</label>
                            <input type="text" id="eo-nombre" name="nombreCorto" class="form-input" maxlength="40" required>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">Tipo de Máquina *</label>
                            <select id="eo-maquina" name="maquina" class="form-input" required>
                                <option value="">Seleccione...</option>
                                <option value="Manual">Manual</option>
                                <option value="PLA">Plana (PLA)</option>
                                <option value="FIL">Fileteadora (FIL)</option>
                                <option value="REC">Recubridora (REC)</option>
                                <option value="PLAN">Plana Industrial (PLAN)</option>
                                <option value="DOB">Dobladora (DOB)</option>
                            </select>
                        </div>
                        <div class="form-grupo">
                            <label class="form-label">SAM (minutos estándar) *</label>
                            <input type="number" id="eo-sam" name="samOperacion" class="form-input" step="0.01" min="0.01" required>
                        </div>
                    </div>

                    <div class="form-grupo">
                        <label class="form-label">Detalle de la Operación *</label>
                        <textarea id="eo-detalle" name="detalle" class="form-input" rows="3" maxlength="255" required></textarea>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-secundario" onclick="cerrarModal('modalEditarOperacion')">Cancelar</button>
                        <button type="submit" class="btn-principal">Guardar Cambios</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- Modal simple para ver el detalle completo de una operación --%>
    <div id="modalDetalleOperacion" class="modal-overlay">
        <div class="modal-contenido" style="max-width:500px;">
            <div class="modal-header">
                <h2>Detalle de la Operación</h2>
                <button class="modal-close-btn" onclick="cerrarModal('modalDetalleOperacion')">&times;</button>
            </div>
            <div class="modal-body">
                <p id="vop-detalle" style="white-space:pre-wrap;"></p>
            </div>
        </div>
    </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // ---------------------------------------------------------------
        // Tabs
        // ---------------------------------------------------------------
        function cambiarTab(nombre, boton) {
            document.querySelectorAll('.tab-content').forEach(function(t) {
                t.style.display = 'none';
            });
            document.querySelectorAll('.tab').forEach(function(t) {
                t.classList.remove('active');
            });
            document.getElementById('tab-' + nombre).style.display = 'block';
            boton.classList.add('active');
        }

        // ---------------------------------------------------------------
        // Modales
        // ---------------------------------------------------------------
        function abrirModal(id) {
            document.getElementById(id).classList.add('show');
        }
        function cerrarModal(id) {
            document.getElementById(id).classList.remove('show');
        }
        window.onclick = function(event) {
            if (event.target.classList.contains('modal-overlay')) {
                event.target.classList.remove('show');
            }
        };

        // ---------------------------------------------------------------
        // Lotes
        // ---------------------------------------------------------------
        function verDetalleLote(om, ref, color, remision, oc, fecha, modulo, xxl, xl, l, m, s, xs) {
            document.getElementById('vl-om').textContent      = om;
            document.getElementById('vl-ref').textContent     = ref;
            document.getElementById('vl-color').textContent   = color;
            document.getElementById('vl-remision').textContent = remision;
            document.getElementById('vl-oc').textContent      = oc || '—';
            document.getElementById('vl-fecha').textContent   = fecha;
            document.getElementById('vl-modulo').textContent  = 'Módulo ' + modulo;
            document.getElementById('vl-xxl').textContent = xxl;
            document.getElementById('vl-xl').textContent  = xl;
            document.getElementById('vl-l').textContent   = l;
            document.getElementById('vl-m').textContent   = m;
            document.getElementById('vl-s').textContent   = s;
            document.getElementById('vl-xs').textContent  = xs;
            abrirModal('modalVerLote');
        }

        function editarLote(om, ref, color, remision, oc, fecha, moduloId, xxl, xl, l, m, s, xs) {
            document.getElementById('el-om-hidden').value  = om;
            document.getElementById('el-om-display').value = om;
            document.getElementById('el-remision').value   = remision;
            document.getElementById('el-oc').value         = oc || '';
            document.getElementById('el-color').value      = color;
            document.getElementById('el-referencia').value = ref;
            document.getElementById('el-fecha').value      = fecha;
            document.getElementById('el-modulo').value     = moduloId;
            document.getElementById('el-xxl').value = xxl;
            document.getElementById('el-xl').value  = xl;
            document.getElementById('el-l').value   = l;
            document.getElementById('el-m').value   = m;
            document.getElementById('el-s').value   = s;
            document.getElementById('el-xs').value  = xs;
            abrirModal('modalEditarLote');
        }

        // ---------------------------------------------------------------
        // Referencias — usa data attributes para evitar romper JS con
        // caracteres especiales en los valores del campo texto
        // ---------------------------------------------------------------
        function editarReferencia(boton) {
            document.getElementById('er-id-hidden').value  = boton.dataset.id;
            document.getElementById('er-id-display').value = boton.dataset.id;
            document.getElementById('er-tipo').value       = boton.dataset.tipo;
            document.getElementById('er-cliente').value    = boton.dataset.cliente;
            document.getElementById('er-coleccion').value  = boton.dataset.coleccion || '';
            document.getElementById('er-precio').value     = boton.dataset.precio;
            abrirModal('modalEditarReferencia');
        }

        // ---------------------------------------------------------------
        // Operaciones — usa data attributes por el mismo motivo
        // ---------------------------------------------------------------
        function editarOperacion(boton) {
            document.getElementById('eo-id').value         = boton.dataset.id;
            document.getElementById('eo-referencia').value = boton.dataset.ref;
            document.getElementById('eo-nombre').value     = boton.dataset.nombre;
            document.getElementById('eo-maquina').value    = boton.dataset.maquina;
            document.getElementById('eo-sam').value        = boton.dataset.sam;
            document.getElementById('eo-detalle').value    = boton.dataset.detalle || '';
            abrirModal('modalEditarOperacion');
        }

        function verDetalleOperacion(boton) {
            document.getElementById('vop-detalle').textContent = boton.dataset.detalle || '(sin detalle)';
            abrirModal('modalDetalleOperacion');
        }
    </script>

</body>
</html>
