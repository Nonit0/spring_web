package com.iesvdc.dam.demo2.tienda;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/** Carrito de la sesión: id de camiseta y cantidad. Sobrevive al inicio de sesión. */
@Component
@SessionScope
public class Carrito implements Serializable {

    public static final int MAXIMO_POR_LINEA = 99;

    private final Map<Long, Integer> lineas = new LinkedHashMap<>();

    public int cantidadDe(Long camisetaId) {
        return lineas.getOrDefault(camisetaId, 0);
    }

    public void fijar(Long camisetaId, int cantidad) {
        if (cantidad <= 0) {
            lineas.remove(camisetaId);
        } else {
            lineas.put(camisetaId, Math.min(cantidad, MAXIMO_POR_LINEA));
        }
    }

    public void quitar(Long camisetaId) {
        lineas.remove(camisetaId);
    }

    public void vaciar() {
        lineas.clear();
    }

    public int getUnidades() {
        return lineas.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Long, Integer> getLineas() {
        return Map.copyOf(lineas);
    }
}
