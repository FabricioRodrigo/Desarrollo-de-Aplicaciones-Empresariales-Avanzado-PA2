# Inventario Service — Jakarta EE 11 + SQL Server Express 2025

Proyecto académico complementario de `sistema-pedidos-jsp` para la Sesión 4: **Integración de aplicativos web con componentes empresariales**.

## Stack

- Java 21
- Maven
- Jakarta EE 11
- WildFly 41
- Jakarta REST
- Jakarta Enterprise Beans (EJB)
- Jakarta Persistence 3.2
- Jakarta Transactions (JTA)
- Bean Validation
- Microsoft JDBC Driver for SQL Server 13.6
- SQL Server Express 2025

## Arquitectura

```text
Sistema de Pedidos / Postman
          |
          | HTTP + JSON
          v
ProductoResource (Jakarta REST)
          |
          | @EJB
          v
InventarioService (@Stateless)
          |
          | @PersistenceContext
          v
EntityManager / InventarioPU
          |
          | java:app/jdbc/InventarioDS
          v
SQL Server Express 2025
```

## Preparación rápida

1. Habilitar TCP/IP en SQL Server Express y configurar un puerto fijo (la guía usa 1433).
2. Habilitar autenticación SQL Server/Mixed Mode para el usuario académico.
3. Ejecutar `database/01_crear_inventario.sql` en SSMS.
4. Verificar JDK 21 y Maven.
5. Ejecutar:

```bash
mvn clean test
mvn clean wildfly:run
```

El plugin usa `-Djboss.socket.binding.port-offset=2`, por lo que WildFly escucha en HTTP **8082** y Management **9992**. Esto permite ejecutar a la vez el `sistema-pedidos` existente (HTTP 8081, Management 9990).

Abrir:

```text
http://localhost:8082/inventario-service/
```

API principal:

```text
GET  /inventario-service/api/inventario/productos
GET  /inventario-service/api/inventario/productos/1/stock
POST /inventario-service/api/inventario/productos/1/reservas
```

Body de reserva:

```json
{
  "cantidad": 2
}
```

También existen endpoints por `codigo` (`LAP-001`, `MON-001`, `TEC-001`) para mostrar un contrato menos acoplado a las claves internas de base de datos.

## Material de clase

- `docs/GUIA_IMPLEMENTACION_PASO_A_PASO.md`
- `docs/INTEGRACION_CON_SISTEMA_PEDIDOS.md`
- `requests/inventario-service.http`
- `openapi/inventario-service.yaml`
- `database/02_reset_stock_demo.sql`

## Nota académica de seguridad

`@DataSourceDefinition` contiene usuario/contraseña para reducir pasos durante el laboratorio. En producción las credenciales no deben almacenarse en código; deben externalizarse en un DataSource administrado por WildFly, Credential Store o un mecanismo de secretos. `trustServerCertificate=true` también se usa solo para el laboratorio local.


## Ampliación PA2: devolución de stock

Para la PA2 agregué un endpoint que permite devolver el stock cuando se anula un pedido desde `sistema-pedidos-springboot`.

### Endpoint

```text
POST /inventario-service/api/inventario/productos/codigo/{codigo}/devoluciones
```

Ejemplo:

```http
POST http://localhost:8082/inventario-service/api/inventario/productos/codigo/LAP-001/devoluciones
Content-Type: application/json

{
  "cantidad": 2
}
```

Respuesta exitosa:

```json
{
  "codigo": "LAP-001",
  "cantidadDevuelta": 2,
  "stockRestante": 10
}
```

La API rechaza cantidades iguales o menores que cero y códigos de producto inexistentes mediante respuestas HTTP controladas.

### Arquitectura de la operación

```text
ProductoResource
      |
      | @EJB
      v
InventarioService (@Stateless)
      |
      v
ProductoInventario.devolverStock(...)
      |
      | JPA / JTA
      v
SQL Server - InventarioDB
```

La devolución se guarda en una transacción de Inventario. El atributo @Version se mantiene para controlar actualizaciones simultáneas.

### Configuración

El servicio utiliza:

```text
Java 21
WildFly 41
SQL Server Express
HTTP 8082
Management 9992
```

La URL, usuario y contraseña de SQL Server se configuran en `config/database.properties`. En un entorno real estos valores deben suministrarse mediante variables de entorno o un mecanismo seguro de secretos.

### Ejecución

El orden de arranque es:

1. SQL Server.
2. WildFly con `inventario-service`.
3. `sistema-pedidos-springboot`.

Comandos:

```powershell
mvn test
mvn clean wildfly:run
```

Consulta del catálogo:

```text
GET http://localhost:8082/inventario-service/api/inventario/productos
```

### Pruebas unitarias de PA2

Clase:

```text
src/test/java/pe/edu/isil/inventario/domain/DevolucionStockTest.java
```

Escenarios comprobados:

- Una devolución positiva incrementa exactamente el stock.
- Una devolución igual a cero o negativa es rechazada y no modifica el stock.

Ejecución:

```powershell
mvn test
```
### Pasos de reproducción

1. Ejecutar `database/01_crear_inventario.sql` en SQL Server.
2. Iniciar WildFly con `mvn clean wildfly:run`.
3. Consultar el stock de `LAP-001`.
4. Enviar una devolución positiva al endpoint de devoluciones.
5. Comprobar que el stock aumentó exactamente en la cantidad enviada.
6. Enviar una cantidad igual a cero y comprobar que la API responde con un error sin modificar el stock.

### Limitaciones

- La transacción local únicamente controla los cambios de `InventarioDB`.
- No existe una transacción distribuida con la base H2 de Pedidos.
- El endpoint `POST` no se reintenta automáticamente porque no posee una clave de idempotencia.
- La configuración académica utiliza `trustServerCertificate=true`; no debe utilizarse de esta forma en producción.