package com.iesvdc.dam.demo2.modelos;

public enum EstadoPedido {
    PENDIENTE("Pendiente"), ENVIADO("Enviado"), ENTREGADO("Entregado"), CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
