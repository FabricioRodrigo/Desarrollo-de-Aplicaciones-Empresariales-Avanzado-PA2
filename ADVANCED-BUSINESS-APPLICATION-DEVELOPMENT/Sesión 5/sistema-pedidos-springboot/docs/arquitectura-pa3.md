# Arquitectura PA3

El módulo conserva Spring Data JPA para registrar pedidos y utiliza Spring JDBC exclusivamente para las consultas nuevas de supervisión.

```mermaid
flowchart LR
    U[Navegador o cliente HTTP] --> SS[Spring Security]
    SS --> DS[DispatcherServlet]

    DS --> WC[PedidoReporteWebController]
    DS --> RC[PedidoReporteController]
    WC --> JSP[reportes-pedidos.jsp]

    WC --> S[PedidoReporteService]
    RC --> S

    S --> JDBC[PedidoReporteJdbcRepository]
    JDBC --> NP[NamedParameterJdbcTemplate]
    JDBC --> JT[JdbcTemplate]
    NP --> MYSQL[(MySQL pedidosdb)]
    JT --> MYSQL

    DS --> PC[PedidoController]
    PC --> PS[PedidoService]
    PS --> JPA[PedidoRepository JPA]
    JPA --> MYSQL

    PS --> IRC[InventarioRestClient]
    IRC -->|HTTP JSON| INV[inventario-service]
    INV --> SQL[(SQL Server InventarioDB)]
```

## Responsabilidades

- `Spring Security` autentica y autoriza antes de que la solicitud alcance los controladores.
- `PedidoReporteWebController` carga el `Model` y selecciona la vista JSP.
- `PedidoReporteController` publica las representaciones JSON y traduce validaciones a HTTP 400.
- `PedidoReporteService` concentra las reglas de validación y evita SQL en la capa web.
- `PedidoReporteJdbcRepository` ejecuta SQL parametrizado y mapea resultados simples o agregados.
- `PedidoRepository` conserva el flujo transaccional existente mediante Spring Data JPA.
- `InventarioRestClient` mantiene la integración externa usada al registrar pedidos.

## Frontera transaccional

La transacción local de Spring cubre los cambios de pedidos en MySQL. La reserva efectuada por HTTP en `inventario-service` pertenece a otro proceso y a otra base; no existe una transacción distribuida entre MySQL y SQL Server.
