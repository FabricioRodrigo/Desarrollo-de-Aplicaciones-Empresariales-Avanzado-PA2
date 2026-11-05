package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controlador MVC de la pantalla de consulta y supervision */

@Controller
@RequestMapping("/reportes/pedidos")
public class PedidoReporteWebController {

    private final PedidoReporteService reporteService;

    public PedidoReporteWebController(
            PedidoReporteService reporteService
    ) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) BigDecimal totalMinimo,
            @RequestParam(required = false) Integer minPedidos,
            Model model,
            HttpServletResponse response
    ) {
        model.addAttribute(
                "resumen",
                reporteService.obtenerResumen()
        );

        model.addAttribute("clienteIngresado", cliente);
        model.addAttribute("totalMinimoIngresado", totalMinimo);
        model.addAttribute("minPedidosIngresado", minPedidos);

        try {
            if (cliente != null || totalMinimo != null) {
                model.addAttribute(
                        "pedidosBusqueda",
                        reporteService
                                .buscarPedidosPorClienteYTotalMinimo(
                                        cliente,
                                        totalMinimo
                                )
                );
                model.addAttribute("busquedaEjecutada", true);
            }

            if (minPedidos != null) {
                model.addAttribute(
                        "clientesFrecuentes",
                        reporteService.listarClientesFrecuentes(
                                minPedidos
                        )
                );
                model.addAttribute(
                        "clientesFrecuentesEjecutado",
                        true
                );
            }
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("error", exception.getMessage());
        }

        return "reportes-pedidos";
    }
}
