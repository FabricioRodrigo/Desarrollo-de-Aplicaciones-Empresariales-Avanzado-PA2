package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionRequest;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.AnulacionException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnulacionPedidoServiceImplTest {

    private PedidoRepository pedidoRepository;
    private AnulacionPedidoRepository anulacionRepository;
    private DevolucionStockClient devolucionStockClient;
    private AnulacionPedidoServiceImpl service;
    private Pedido pedido;

    @BeforeEach
    void preparar() {
        pedidoRepository = mock(PedidoRepository.class);
        anulacionRepository =
                mock(AnulacionPedidoRepository.class);
        devolucionStockClient =
                mock(DevolucionStockClient.class);

        service = new AnulacionPedidoServiceImpl(
                pedidoRepository,
                anulacionRepository,
                devolucionStockClient
        );

        pedido = new Pedido(
                "Cliente de prueba",
                "LAP-001",
                "Laptop",
                new BigDecimal("2500.00"),
                2,
                new BigDecimal("5000.00")
        );

        pedido.setId(1L);
    }

    @Test
    void anulacionCorrectaDevuelveStockYGuardaCambios() {
        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        when(anulacionRepository.findByPedidoId(1L))
                .thenReturn(Optional.empty());

        DevolucionStockResponse devolucion =
                new DevolucionStockResponse();

        devolucion.setCodigo("LAP-001");
        devolucion.setCantidadDevuelta(2);
        devolucion.setStockRestante(10);

        when(
                devolucionStockClient.devolverStock(
                        "LAP-001",
                        2
                )
        ).thenReturn(devolucion);

        when(pedidoRepository.save(pedido))
                .thenReturn(pedido);

        when(anulacionRepository.save(
                any(AnulacionPedido.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        var resultado = service.anular(
                new AnulacionRequest(
                        1L,
                        "Solicitud del cliente"
                )
        );

        assertEquals(
                PedidoEstado.ANULADO,
                pedido.getEstado()
        );

        assertEquals(1L, resultado.getPedidoId());
        assertEquals(
                "LAP-001",
                resultado.getProductoCodigo()
        );
        assertEquals(2, resultado.getCantidadDevuelta());
        assertEquals(
                "Solicitud del cliente",
                resultado.getMotivo()
        );

        verify(
                devolucionStockClient,
                times(1)
        ).devolverStock("LAP-001", 2);

        verify(pedidoRepository).save(pedido);

        verify(anulacionRepository).save(
                any(AnulacionPedido.class)
        );
    }

    @Test
    void anulacionDuplicadaSeRechazaSinDevolverStock() {
        // Preparación
        pedido.anular();

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        AnulacionException error = assertThrows(
                AnulacionException.class,
                () -> service.anular(
                        new AnulacionRequest(1L, null)
                )
        );

        assertEquals(
                AnulacionException.Tipo.ANULACION_DUPLICADA,
                error.getTipo()
        );

        verifyNoInteractions(devolucionStockClient);

        verify(
                pedidoRepository,
                never()
        ).save(any(Pedido.class));

        verify(
                anulacionRepository,
                never()
        ).save(any(AnulacionPedido.class));
    }

    @Test
    void falloInventarioNoConfirmaCambiosLocales() {
        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        when(anulacionRepository.findByPedidoId(1L))
                .thenReturn(Optional.empty());

        when(
                devolucionStockClient.devolverStock(
                        "LAP-001",
                        2
                )
        ).thenThrow(
                new InventarioNoDisponibleException(
                        "Inventario no disponible.",
                        null
                )
        );

        assertThrows(
                InventarioNoDisponibleException.class,
                () -> service.anular(
                        new AnulacionRequest(1L, null)
                )
        );

        assertEquals(
                PedidoEstado.REGISTRADO,
                pedido.getEstado()
        );

        verify(
                devolucionStockClient,
                times(1)
        ).devolverStock("LAP-001", 2);

        verify(
                pedidoRepository,
                never()
        ).save(any(Pedido.class));

        verify(
                anulacionRepository,
                never()
        ).save(any(AnulacionPedido.class));
    }
}
