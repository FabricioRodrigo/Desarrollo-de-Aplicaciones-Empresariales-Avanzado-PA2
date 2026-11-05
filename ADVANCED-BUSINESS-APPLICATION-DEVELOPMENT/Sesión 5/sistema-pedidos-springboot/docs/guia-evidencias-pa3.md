# Guía de evidencias PA3

Esta guía organiza las capturas obligatorias. Todos los casos se ejecutan sobre la misma base MySQL y la rama evaluada.

## Preparación común

1. Iniciar MySQL y comprobar `pedidosdb`.
2. Iniciar SQL Server y `inventario-service` en WildFly con Java 21.
3. Iniciar Spring Boot con `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
4. Registrar como `OPERADOR` al menos tres pedidos para dos clientes. Un cliente debe tener dos pedidos y el otro uno.
5. Conservar en cada captura la URL, parámetros, respuesta o tabla y código HTTP cuando corresponda.

## Caso 1: búsqueda por cliente y monto mínimo

- JSP: `/reportes/pedidos?cliente=Martin&totalMinimo=1000`
- JSON: `/api/v1/pedidos/busqueda-avanzada?cliente=Martin&totalMinimo=1000`
- MySQL: consultar los pedidos del cliente y comprobar cuáles cumplen `total >= 1000`.
- Evidencia esperada: solo aparecen los pedidos que cumplen simultáneamente ambos filtros.
- Recorrido: `PedidoReporteWebController` o `PedidoReporteController` → `PedidoReporteService` → `PedidoReporteJdbcRepository` → MySQL.

## Caso 2: clientes frecuentes y consolidado

- JSP: `/reportes/pedidos?minPedidos=2`
- JSON: `/api/v1/pedidos/clientes-frecuentes?minPedidos=2`
- MySQL: agrupar por cliente y contrastar conteo, unidades, suma y promedio.
- Evidencia esperada: solo aparece el cliente con dos o más pedidos, con `totalPedidos`, `totalUnidades`, `importeAcumulado` y `ticketPromedio` correctos.
- Recorrido: Controller → Service → repositorio JDBC con `GROUP BY/HAVING` → MySQL.

## Caso 3: parámetros inválidos

Ejecutar y capturar las tres variantes:

- `cliente` vacío en búsqueda avanzada.
- `totalMinimo=-1`.
- `minPedidos=0`.

La API debe responder HTTP 400 con un `ProblemDetail`; la JSP debe presentar un mensaje controlado. La validación ocurre en el Service antes de invocar el repositorio.

## Caso 4: inventario indisponible

1. Detener `inventario-service`.
2. Intentar registrar un pedido nuevo como `OPERADOR`.
3. Capturar el mensaje controlado de indisponibilidad.
4. Sin reiniciar Inventario, abrir `/reportes/pedidos` y comprobar que los datos almacenados siguen disponibles.
5. Restaurar `inventario-service` al terminar.

El registro usa `PedidoController` → `PedidoService` → `InventarioRestClient`; los reportes no dependen de Inventario porque consultan MySQL local.

## Caso 5: roles y CSRF

Capturar cuatro verificaciones:

1. Usuario anónimo abre `/reportes/pedidos` y es redirigido al login.
2. `CONSULTA` inicia sesión y visualiza los reportes.
3. `CONSULTA` intenta `POST /pedidos` con CSRF válido y recibe HTTP 403.
4. `OPERADOR` intenta `POST /pedidos` sin CSRF y recibe HTTP 403.

Estas decisiones corresponden al filtro de Spring Security antes de la ejecución del controlador.

## Evidencia automatizada

Ejecutar:

```powershell
.\mvnw.cmd clean test
```

Capturar la sección final con el total de pruebas, cero fallos, cero errores y `BUILD SUCCESS`. Registrar también la URL del repositorio, la rama `feature/pa3-fabricio` y el hash del commit final.
