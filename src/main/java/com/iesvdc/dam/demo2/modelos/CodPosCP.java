package com.iesvdc.dam.demo2.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CodPosCP {
    
    // El Código Postal es la clave primaria
    @Id
    @Column(length = 5)
    private String codigoPostal;

    @Column(nullable = false, length = 100)
    private String municipio;

    @Column(nullable = false, length = 50)
    private String provincia;
}