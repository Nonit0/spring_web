package com.iesvdc.dam.demo2.modelos;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cada fila es una variante vendible: un modelo con una talla y un color concretos. */
@Entity
@Data
@NoArgsConstructor
public class Camiseta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pon un nombre")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombre;

    @jakarta.validation.constraints.NotNull(message = "Elige una talla")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Talla talla;

    @jakarta.validation.constraints.NotNull(message = "Elige un color")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ColorCamiseta color;

    @jakarta.validation.constraints.NotNull(message = "Pon un precio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 6, fraction = 2, message = "Usa como máximo dos decimales")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private int stock;

    public boolean isAgotada() {
        return stock <= 0;
    }
}
