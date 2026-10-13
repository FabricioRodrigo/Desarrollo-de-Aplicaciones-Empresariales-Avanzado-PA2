package com.isil.edu.pe.spedidos.exception;

/** Excepción de negocio del flujo de anulaciones */
public class AnulacionException extends RuntimeException {

    public enum Tipo {
        DATOS_INVALIDOS,
        PEDIDO_NO_ENCONTRADO,
        ANULACION_DUPLICADA
    }

    private final Tipo tipo;

    public AnulacionException(
            Tipo tipo,
            String message
    ) {
        super(message);
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}
