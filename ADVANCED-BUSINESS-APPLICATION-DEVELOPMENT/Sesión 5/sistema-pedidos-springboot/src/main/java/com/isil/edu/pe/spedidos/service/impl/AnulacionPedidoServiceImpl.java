package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionRequest;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionResponse;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.AnulacionException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AnulacionPedidoServiceImpl
        implements AnulacionPedidoService {

    private final PedidoRepository pedidoRepository;
    private final AnulacionPedidoRepository anulacionRepository;
    private final DevolucionStockClient devolucionStockClient;

    public AnulacionPedidoServiceImpl(
            PedidoRepository pedidoRepository,
            AnulacionPedidoRepository anulacionRepository,
            DevolucionStockClient devolucionStockClient
    ) {
        this.pedidoRepository = pedidoRepository;
        this.anulacionRepository = anulacionRepository;
        this.devolucionStockClient = devolucionStockClient;
    }

    @Override
    @Transactional
    public AnulacionResponse anular(
            AnulacionRequest request
    ) {
        validarSolicitud(request);

        Pedido pedido = pedidoRepository
                .findById(request.getPedidoId())
                .orElseThrow(
                        () -> new AnulacionException(
                                AnulacionException.Tipo.PEDIDO_NO_ENCONTRADO,
                                "El pedido con ID "
                                        + request.getPedidoId()
                                        + " no existe."
                        )
                );

        if (pedido.getEstado() != PedidoEstado.REGISTRADO) {
            throw new AnulacionException(
                    AnulacionException.Tipo.ANULACION_DUPLICADA,
                    "El pedido ya fue anulado."
            );
        }

        if (anulacionRepository
                .findByPedidoId(pedido.getId())
                .isPresent()) {
            throw new AnulacionException(
                    AnulacionException.Tipo.ANULACION_DUPLICADA,
                    "El pedido ya tiene una anulación registrada."
            );
        }

        DevolucionStockResponse devolucion =
                devolucionStockClient.devolverStock(
                        pedido.getProductoCodigo(),
                        pedido.getCantidad()
                );

        validarDevolucion(pedido, devolucion);

        pedido.anular();

        AnulacionPedido anulacion =
                new AnulacionPedido(
                        pedido.getId(),
                        pedido.getProductoCodigo(),
                        pedido.getCantidad(),
                        normalizarMotivo(request.getMotivo())
                );

        pedidoRepository.save(pedido);

        AnulacionPedido anulacionGuardada =
                anulacionRepository.save(anulacion);

        return AnulacionResponse.from(anulacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnulacionResponse> listar() {
        return anulacionRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(AnulacionResponse::from)
                .toList();
    }

    private void validarSolicitud(
            AnulacionRequest request
    ) {
        if (request == null
                || request.getPedidoId() == null
                || request.getPedidoId() <= 0) {
            throw new AnulacionException(
                    AnulacionException.Tipo.DATOS_INVALIDOS,
                    "El identificador del pedido debe ser positivo."
            );
        }

        if (request.getMotivo() != null
                && request.getMotivo().length() > 500) {
            throw new AnulacionException(
                    AnulacionException.Tipo.DATOS_INVALIDOS,
                    "El motivo admite como máximo 500 caracteres."
            );
        }
    }

    private void validarDevolucion(
            Pedido pedido,
            DevolucionStockResponse devolucion
    ) {
        if (devolucion == null
                || devolucion.getCodigo() == null
                || !pedido.getProductoCodigo().equals(
                devolucion.getCodigo()
        )
                || devolucion.getCantidadDevuelta()
                != pedido.getCantidad()) {
            throw new InventarioNoDisponibleException(
                    "Inventario devolvió una respuesta "
                            + "incompleta o inconsistente.",
                    null
            );
        }
    }

    private String normalizarMotivo(
            String motivo
    ) {
        if (motivo == null || motivo.isBlank()) {
            return null;
        }

        return motivo.trim();
    }
}
