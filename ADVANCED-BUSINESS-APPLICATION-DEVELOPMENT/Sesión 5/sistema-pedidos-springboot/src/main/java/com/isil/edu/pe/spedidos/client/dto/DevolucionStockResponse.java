package com.isil.edu.pe.spedidos.client.dto;

/**Respuesta recibida desde inventario-service después de devolver unidades al stock */

public class DevolucionStockResponse {

    private String codigo;
    private int cantidadDevuelta;
    private int stockRestante;

    public DevolucionStockResponse() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(int cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public int getStockRestante() {
        return stockRestante;
    }

    public void setStockRestante(int stockRestante) {
        this.stockRestante = stockRestante;
    }
}
