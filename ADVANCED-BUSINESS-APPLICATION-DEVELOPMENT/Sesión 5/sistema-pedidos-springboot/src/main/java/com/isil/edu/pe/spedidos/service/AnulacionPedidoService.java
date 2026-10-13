package com.isil.edu.pe.spedidos.service;

import com.isil.edu.pe.spedidos.controller.dto.AnulacionRequest;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionResponse;
import java.util.List;

/** Operaciones de negocio relacionadas con la anulación de pedidos */

public interface AnulacionPedidoService {

    /** Anula un pedido y devuelve sus unidades al inventario */
    AnulacionResponse anular(
            AnulacionRequest request
    );

    /** Lista todas las anulaciones registradas */
    List<AnulacionResponse> listar();
}
