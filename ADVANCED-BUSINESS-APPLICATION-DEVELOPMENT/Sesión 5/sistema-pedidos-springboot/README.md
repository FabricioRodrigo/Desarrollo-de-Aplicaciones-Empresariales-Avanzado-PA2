## Ampliación PA2: anulación de pedidos

Para la PA2 agregué un flujo de anulación que devuelve el stock mediante `inventario-service`, cambia el pedido a estado `ANULADO` y registra la operación en H2.

### Endpoints

Registrar una anulación:

```text
POST /sistema-pedidos/api/v1/anulaciones
```

Ejemplo:

```http
POST http://localhost:8081/sistema-pedidos/api/v1/anulaciones
Content-Type: application/json

{
  "pedidoId": 1,
  "motivo": "Solicitud del cliente"
}
```

Consultar las anulaciones:

```text
GET /sistema-pedidos/api/v1/anulaciones
```

Consultar los pedidos y sus estados:

```text
GET /sistema-pedidos/api/v1/orders
```

### Flujo implementado

```text
AnulacionPedidoController
          |
          v
AnulacionPedidoService
       /         \
      v           v
Repositories   DevolucionStockClient
      |                 |
      v                 | HTTP/JSON
     H2                 v
                inventario-service
```

El servicio valida que el pedido exista y tenga estado `REGISTRADO`. Después solicita la devolución remota, actualiza el pedido a `ANULADO` y guarda `AnulacionPedido`.

La restricción `UNIQUE` sobre `pedidoId` y la validación del estado evitan anulaciones duplicadas en el flujo secuencial evaluado.

### Requisitos

- Java 21.
- Maven Wrapper incluido en el proyecto.
- SQL Server con `InventarioDB`.
- `inventario-service` ejecutándose en WildFly por el puerto 8082.
- Postman u otro cliente HTTP para las pruebas funcionales.

### Configuración

La URL de Inventario se encuentra en `src/main/resources/application.properties`:

```properties
inventario.api.base-url=http://localhost:8082/inventario-service/api/inventario
inventario.api.connect-timeout-ms=2000
inventario.api.read-timeout-ms=3000
```

Orden de arranque:

1. SQL Server.
2. `inventario-service` en WildFly.
3. `sistema-pedidos-springboot`.

Ejecución:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

La aplicación se ejecuta en:

```text
http://localhost:8081/sistema-pedidos
```

### Pruebas unitarias de PA2

Clase:

```text
src/test/java/com/isil/edu/pe/spedidos/service/impl/AnulacionPedidoServiceImplTest.java
```

Escenarios comprobados:

- Anulación correcta y una sola devolución remota.
- Rechazo de una anulación duplicada.
- Fallo de Inventario sin guardado local posterior.

### Pasos de reproducción

1. Iniciar SQL Server.
2. Iniciar `inventario-service` en WildFly.
3. Iniciar Spring Boot con `.\mvnw.cmd spring-boot:run`.
4. Registrar un pedido de dos unidades desde `/sistema-pedidos/pedidos`.
5. Consultar el pedido mediante `GET /api/v1/orders`.
6. Anularlo mediante `POST /api/v1/anulaciones`.
7. Verificar que el pedido quede `ANULADO`, exista un registro de auditoría y el stock sea restituido.
8. Repetir la anulación para comprobar la respuesta HTTP 409.
9. Intentar anular un identificador inexistente para comprobar la respuesta HTTP 404.
10. Detener Inventario e intentar anular otro pedido para comprobar la respuesta HTTP 503 y que el pedido permanezca `REGISTRADO`.

### Limitaciones

- La transacción de Spring controla únicamente H2.
- La devolución remota y la escritura local no forman una transacción distribuida.
- Los `POST` no tienen reintentos automáticos.
- La solución no garantiza idempotencia frente a solicitudes concurrentes.
- No se implementó el reto opcional de compensación.
