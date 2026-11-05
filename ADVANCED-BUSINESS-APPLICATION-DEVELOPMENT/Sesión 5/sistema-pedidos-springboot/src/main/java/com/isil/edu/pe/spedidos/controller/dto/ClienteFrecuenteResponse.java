package com.isil.edu.pe.spedidos.controller.dto;

import com.isil.edu.pe.spedidos.service.domain.ClienteFrecuente;
import java.math.BigDecimal;

/**
 * Respuesta JSON correspondiente al consolidado de un cliente.
 */
public record ClienteFrecuenteResponse(
        String cliente,
        long totalPedidos,
        long totalUnidades,
        BigDecimal importeAcumulado,
        BigDecimal ticketPromedio
) {

    public static ClienteFrecuenteResponse from(
            ClienteFrecuente cliente
    ) {
        return new ClienteFrecuenteResponse(
                cliente.cliente(),
                cliente.totalPedidos(),
                cliente.totalUnidades(),
                cliente.importeAcumulado(),
                cliente.ticketPromedio()
        );
    }
}
