package com.isil.edu.pe.spedidos.controller.dto;

import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import java.time.LocalDateTime;

/** Datos de una anulacion expuestos mediante la API */

public class AnulacionResponse {

    private Long id;
    private Long pedidoId;
    private String productoCodigo;
    private int cantidadDevuelta;
    private LocalDateTime fecha;
    private String motivo;

    public AnulacionResponse() {
    }

    public AnulacionResponse(
            Long id,
            Long pedidoId,
            String productoCodigo,
            int cantidadDevuelta,
            LocalDateTime fecha,
            String motivo
    ) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.productoCodigo = productoCodigo;
        this.cantidadDevuelta = cantidadDevuelta;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    /** Convierte la entidad JPA en un DTO de respuesta */

    public static AnulacionResponse from(
            AnulacionPedido anulacion
    ) {
        return new AnulacionResponse(
                anulacion.getId(),
                anulacion.getPedidoId(),
                anulacion.getProductoCodigo(),
                anulacion.getCantidadDevuelta(),
                anulacion.getFecha(),
                anulacion.getMotivo()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public int getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }
}
