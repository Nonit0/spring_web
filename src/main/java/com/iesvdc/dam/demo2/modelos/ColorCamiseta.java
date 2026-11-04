package com.iesvdc.dam.demo2.modelos;

public enum ColorCamiseta {
    BLANCO("Blanco", "#FFFFFF"), NEGRO("Negro", "#1A1A1A"), GRIS("Gris", "#8E8E8E"),
    ROJO("Rojo", "#E5383B"), AZUL("Azul", "#2F6BFF"), AZUL_MARINO("Azul marino", "#1D2F6F"),
    VERDE("Verde", "#2FA84F"), AMARILLO("Amarillo", "#FFD23F"), NARANJA("Naranja", "#FF8C32"),
    MORADO("Morado", "#7B4FE0"), ROSA("Rosa", "#FF7EB6"), MARRON("Marrón", "#7A4B2A");

    private final String etiqueta;
    private final String hex;

    ColorCamiseta(String etiqueta, String hex) {
        this.etiqueta = etiqueta;
        this.hex = hex;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getHex() {
        return hex;
    }
}
