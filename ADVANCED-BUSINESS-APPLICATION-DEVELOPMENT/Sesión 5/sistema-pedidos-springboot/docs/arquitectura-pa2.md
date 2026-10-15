# Arquitectura PA2

```mermaid
flowchart TB
    U["Postman / Usuario"] -->|"POST /api/v1/anulaciones"| C["AnulacionPedidoController"]

    C -->|"Inyeccion por constructor"| S["AnulacionPedidoServiceImpl"]

    S -->|"Inyeccion por constructor"| PR["PedidoRepository"]
    S -->|"Inyeccion por constructor"| AR["AnulacionPedidoRepository"]

    PR --> H2[("H2 - Pedidos")]
    AR --> H2

    S -->|"Inyeccion por constructor"| DC["DevolucionStockClient"]
    DC --> RC["DevolucionStockRestClient"]

    RC -->|"HTTP POST y JSON"| JR["ProductoResource"]
    JR -->|"EJB"| IS["InventarioService - Stateless"]
    IS --> PI["ProductoInventario - JPA"]
    PI --> SQL[("SQL Server - InventarioDB")]

    S -->|"Cambia REGISTRADO a ANULADO"| PR
    S -->|"Guarda auditoria"| AR
```

## Responsabilidades

- `AnulacionPedidoController` recibe las solicitudes HTTP.
- `AnulacionPedidoServiceImpl` valida y coordina la anulación.
- `DevolucionStockClient` desacopla Spring Boot del contrato remoto.
- `ProductoResource` expone la devolución mediante Jakarta REST.
- `InventarioService` ejecuta la transacción local de Inventario.
- `PedidoRepository` y `AnulacionPedidoRepository` guardan los cambios en H2.
- `ProductoInventario` actualiza el stock en SQL Server.
