<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <title>Reportes de pedidos</title>

    <c:url var="cssUrl" value="/assets/css/app.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
<div class="contenedor">

    <header class="cabecera">
        <div>
            <p class="eyebrow">
                Spring MVC · Spring JDBC · MySQL
            </p>

            <h1>Reportes de pedidos</h1>

            <p class="subtitulo">
                Consulta y supervisión de la información
                almacenada en el Sistema de Pedidos.
            </p>
        </div>

        <div>
            <c:url var="pedidosUrl" value="/pedidos"/>
            <a href="${pedidosUrl}">Volver a pedidos</a>
        </div>

        <c:url var="logoutUrl" value="/logout"/>
        <form method="post" action="${logoutUrl}">
            <input
                    type="hidden"
                    name="${_csrf.parameterName}"
                    value="${_csrf.token}">

            <button type="submit">
                Cerrar sesión
            </button>
        </form>
    </header>

    <c:if test="${not empty error}">
        <section class="tarjeta">
            <div class="alerta error">
                <c:out value="${error}"/>
            </div>
        </section>
    </c:if>

    <section class="tarjeta">
        <h2>Resumen general</h2>

        <div class="tabla-scroll">
            <table>
                <thead>
                <tr>
                    <th>Total de pedidos</th>
                    <th>Total de unidades</th>
                    <th>Importe acumulado</th>
                    <th>Ticket promedio</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>
                        <c:out value="${resumen.totalPedidos}"/>
                    </td>
                    <td>
                        <c:out value="${resumen.totalUnidades}"/>
                    </td>
                    <td>
                        S/
                        <fmt:formatNumber
                                value="${resumen.importeAcumulado}"
                                minFractionDigits="2"
                                maxFractionDigits="2"/>
                    </td>
                    <td>
                        S/
                        <fmt:formatNumber
                                value="${resumen.ticketPromedio}"
                                minFractionDigits="2"
                                maxFractionDigits="2"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </section>

    <section class="tarjeta">
        <h2>Búsqueda avanzada</h2>

        <c:url
                var="reportesUrl"
                value="/reportes/pedidos"/>

        <form
                method="get"
                action="${reportesUrl}"
                class="form-grid">

            <label>
                Cliente
                <input
                        name="cliente"
                        required
                        maxlength="120"
                        placeholder="Ej. Ana"
                        value="${fn:escapeXml(clienteIngresado)}">
            </label>

            <label>
                Total mínimo
                <input
                        name="totalMinimo"
                        type="number"
                        min="0"
                        step="0.01"
                        required
                        value="${totalMinimoIngresado}">
            </label>

            <button type="submit">
                Buscar pedidos
            </button>
        </form>

        <c:if test="${busquedaEjecutada}">
            <div class="tabla-scroll">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Código</th>
                        <th>Producto</th>
                        <th>Cantidad</th>
                        <th>Total</th>
                        <th>Fecha</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach
                            var="pedido"
                            items="${pedidosBusqueda}">
                        <tr>
                            <td>
                                <c:out value="${pedido.id}"/>
                            </td>
                            <td>
                                <c:out value="${pedido.cliente}"/>
                            </td>
                            <td>
                                <c:out
                                        value="${pedido.productoCodigo}"/>
                            </td>
                            <td>
                                <c:out
                                        value="${pedido.productoNombre}"/>
                            </td>
                            <td>
                                <c:out value="${pedido.cantidad}"/>
                            </td>
                            <td>
                                S/
                                <fmt:formatNumber
                                        value="${pedido.total}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                            </td>
                            <td>
                                <c:out value="${pedido.fecha}"/>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty pedidosBusqueda}">
                        <tr>
                            <td colspan="7" class="sin-datos">
                                No se encontraron pedidos
                                con los filtros indicados.
                            </td>
                        </tr>
                    </c:if>

                    </tbody>
                </table>
            </div>
        </c:if>
    </section>

    <section class="tarjeta">
        <h2>Clientes frecuentes</h2>

        <form
                method="get"
                action="${reportesUrl}"
                class="form-grid">

            <label>
                Cantidad mínima de pedidos
                <input
                        name="minPedidos"
                        type="number"
                        min="1"
                        required
                        value="${empty minPedidosIngresado
                            ? 2
                            : minPedidosIngresado}">
            </label>

            <button type="submit">
                Consultar clientes
            </button>
        </form>

        <c:if test="${clientesFrecuentesEjecutado}">
            <div class="tabla-scroll">
                <table>
                    <thead>
                    <tr>
                        <th>Cliente</th>
                        <th>Total de pedidos</th>
                        <th>Total de unidades</th>
                        <th>Importe acumulado</th>
                        <th>Ticket promedio</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach
                            var="cliente"
                            items="${clientesFrecuentes}">
                        <tr>
                            <td>
                                <c:out value="${cliente.cliente}"/>
                            </td>
                            <td>
                                <c:out
                                        value="${cliente.totalPedidos}"/>
                            </td>
                            <td>
                                <c:out
                                        value="${cliente.totalUnidades}"/>
                            </td>
                            <td>
                                S/
                                <fmt:formatNumber
                                        value="${cliente.importeAcumulado}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                            </td>
                            <td>
                                S/
                                <fmt:formatNumber
                                        value="${cliente.ticketPromedio}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty clientesFrecuentes}">
                        <tr>
                            <td colspan="5" class="sin-datos">
                                Ningún cliente cumple
                                el umbral indicado.
                            </td>
                        </tr>
                    </c:if>

                    </tbody>
                </table>
            </div>
        </c:if>
    </section>

</div>
</body>
</html>
