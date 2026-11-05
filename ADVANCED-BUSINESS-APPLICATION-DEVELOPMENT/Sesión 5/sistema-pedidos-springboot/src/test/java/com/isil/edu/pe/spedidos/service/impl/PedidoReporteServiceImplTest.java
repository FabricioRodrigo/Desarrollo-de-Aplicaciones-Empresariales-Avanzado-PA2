package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoReporteServiceImplTest {

    @Mock
    private PedidoReporteJdbcRepository repository;

    private PedidoReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PedidoReporteServiceImpl(repository);
    }

    @Test
    @DisplayName("Reject invalid minimum orders from report service without querying repository")
    void rejectInvalidMinimumOrdersFromReportServiceWithoutQueryingRepository() {
        int minPedidos = 0;

        assertThrows(
                IllegalArgumentException.class,
                () -> service.listarClientesFrecuentes(minPedidos)
        );

        verify(repository, never())
                .listarClientesFrecuentes(minPedidos);
    }
}
