package com.isil.edu.pe.spedidos.client;

import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;

/**Contrato para solicitar devoluciones de stock al sistema de Inventario */

public interface DevolucionStockClient {

    /**Devuelve unidades al inventario remoto */
    DevolucionStockResponse devolverStock(
            String codigo,
            int cantidad
    );
}
