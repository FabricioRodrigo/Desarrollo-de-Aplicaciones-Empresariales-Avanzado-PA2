package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.controller.dto.AnulacionRequest;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionResponse;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API REST para registrar y consultar anulaciones */
@RestController
@RequestMapping("/api/v1/anulaciones")
public class AnulacionPedidoController {

    private final AnulacionPedidoService service;

    /** Constructor Injection del servicio de anulaciones */
    public AnulacionPedidoController(
            AnulacionPedidoService service
    ) {
        this.service = service;
    }

    /** Anula un pedido
     * POST /api/v1/anulaciones  */
    @PostMapping
    public AnulacionResponse anular(
            @RequestBody AnulacionRequest request
    ) {
        return service.anular(request);
    }

    /** Lista las anulaciones registradas
     * GET /api/v1/anulaciones  */
    @GetMapping
    public List<AnulacionResponse> listar() {
        return service.listar();
    }
}
