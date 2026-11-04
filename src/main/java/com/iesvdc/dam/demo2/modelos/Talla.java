package com.iesvdc.dam.demo2.modelos;

public enum Talla {
    XS("XS"), S("S"), M("M"), L("L"), XL("XL"), XXL("XXL"),
    NINO_10("Niño 10"), NINA_10("Niña 10"), NINO_14("Niño 14"), NINA_14("Niña 14");

    private final String etiqueta;

    Talla(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
