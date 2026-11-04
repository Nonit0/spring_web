package com.iesvdc.dam.demo2.tienda;

import java.math.BigDecimal;

import com.iesvdc.dam.demo2.modelos.Camiseta;

public record LineaCarrito(Camiseta camiseta, int cantidad) {

    public BigDecimal subtotal() {
        return camiseta.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public boolean sinStockSuficiente() {
        return cantidad > camiseta.getStock();
    }
}
