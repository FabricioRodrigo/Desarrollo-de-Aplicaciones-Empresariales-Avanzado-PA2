package com.isil.edu.pe.spedidos.client;

import com.isil.edu.pe.spedidos.client.dto.DevolucionStockRequest;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**Implementación HTTP del contrato de devolución de stock */
@Component
public class DevolucionStockRestClient
        implements DevolucionStockClient {

    private final RestClient restClient;
    private final String baseUrl;

    public DevolucionStockRestClient(
            @Qualifier("inventarioHttpClient")
            RestClient restClient,

            @Value("${inventario.api.base-url}")
            String baseUrl
    ) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    /** Solicita la devolución de stock mediante HTTP POST */
    @Override
    public DevolucionStockResponse devolverStock(
            String codigo,
            int cantidad
    ) {
        try {
            DevolucionStockResponse response =
                    restClient
                            .post()
                            .uri(
                                    baseUrl
                                            + "/productos/codigo/{codigo}/devoluciones",
                                    codigo
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .body(new DevolucionStockRequest(cantidad))
                            .retrieve()
                            .body(DevolucionStockResponse.class);

            if (response == null) {
                throw new InventarioNoDisponibleException(
                        "Inventario devolvió una respuesta vacía.",
                        null
                );
            }

            return response;
        } catch (RestClientResponseException e) {
            throw new InventarioClientException(
                    e.getStatusCode().value(),
                    "Inventario rechazó la devolución de stock. HTTP "
                            + e.getStatusCode().value()
                            + "."
            );
        } catch (ResourceAccessException e) {
            throw new InventarioNoDisponibleException(
                    "El servicio de inventario no está disponible "
                            + "o excedió el tiempo de espera.",
                    e
            );
        } catch (RestClientException e) {
            throw new InventarioNoDisponibleException(
                    "No fue posible procesar la respuesta "
                            + "del servicio de inventario.",
                    e
            );
        }
    }
}
