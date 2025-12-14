package com.iesvdc.dam.demo2.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class CodPos {
    @Id
    private Integer cp;
    @ToString.Exclude // evita recursividad infinita entre provincia y codpos
    @ManyToOne(optional = false)
    private Provincia provincia; // clave foranea que hace referencia al código de la provincia
    @Column(nullable = false, length = 50)
    private String municipio;

    @Transient // 1. Indica a JPA/Hibernate que ignore este campo
    private Integer provinciaId; 

    // 2. Este getter es NECESARIO para que Thymeleaf pueda seleccionar
    //    la opción correcta en la vista de EDICIÓN.
    public Integer getProvinciaId() {
        if (this.provincia != null) {
            // Asume que el ID de Provincia es 'codigo'
            return this.provincia.getCodigo(); 
        }
        // Cuando se envía el formulario, Lombok usa este método para dar valor
        // a 'provinciaId' antes de que el controlador lo use.
        return provinciaId; 
    }
}
