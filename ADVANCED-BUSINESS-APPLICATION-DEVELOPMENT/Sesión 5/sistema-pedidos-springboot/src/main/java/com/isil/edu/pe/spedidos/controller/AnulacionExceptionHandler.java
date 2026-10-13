package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.InventarioClientException;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.exception.AnulacionException;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convierte las excepciones del flujo de anulación en respuestas HTTP controladas. */
@RestControllerAdvice(
        assignableTypes = AnulacionPedidoController.class
)
public class AnulacionExceptionHandler {
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {
    }

    @ExceptionHandler(AnulacionException.class)
    public ResponseEntity<ErrorResponse> manejarAnulacion(
            AnulacionException exception
    ) {
        HttpStatus status = switch (exception.getTipo()) {
            case DATOS_INVALIDOS -> HttpStatus.BAD_REQUEST;
            case PEDIDO_NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case ANULACION_DUPLICADA -> HttpStatus.CONFLICT;
        };

        return crearRespuesta(
                status,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InventarioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> manejarNoDisponible(
            InventarioNoDisponibleException exception
    ) {
        return crearRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InventarioClientException.class)
    public ResponseEntity<ErrorResponse> manejarErrorRemoto(
            InventarioClientException exception
    ) {
        HttpStatus status;

        if (exception.getStatus() >= 500) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else {
            status = HttpStatus.BAD_GATEWAY;
        }

        return crearRespuesta(
                status,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicadoEnBase(
            DataIntegrityViolationException exception
    ) {
        return crearRespuesta(
                HttpStatus.CONFLICT,
                "El pedido ya tiene una anulación registrada."
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(
            HttpMessageNotReadableException exception
    ) {
        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "La solicitud JSON es inválida."
        );
    }

    private ResponseEntity<ErrorResponse> crearRespuesta(
            HttpStatus status,
            String message
    ) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}
