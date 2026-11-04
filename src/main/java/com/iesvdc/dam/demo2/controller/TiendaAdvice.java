package com.iesvdc.dam.demo2.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.iesvdc.dam.demo2.tienda.Carrito;

/** Da a todas las páginas el contador del carrito que se ve en la barra superior. */
@ControllerAdvice
public class TiendaAdvice {

    private final Carrito carrito;

    public TiendaAdvice(Carrito carrito) {
        this.carrito = carrito;
    }

    @ModelAttribute("carritoUnidades")
    public int carritoUnidades() {
        return carrito.getUnidades();
    }
}
