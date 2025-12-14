package com.iesvdc.dam.demo2.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// Importaciones de validación eliminadas: jakarta.validation.constraints.*
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Direccion {
    
    // Tipo de Vía (Usaremos un Enum para simplificar)
    public enum TipoVia {
        CALLE, AVENIDA, PLAZA, PASEO, GLORIETA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CLAVE FORÁNEA USUARIO (Maestro) mapeada a Long
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; 
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVia tipoVia;
    
    @Column(nullable = false, length = 50)
    private String nombreVia;
    
    @Column(nullable = false)
    private int numero;
    
    // CÓDIGO POSTAL mapeado a String
    @Column(name = "cod_pos_cp", nullable = false, length = 50)
    private String codPosCP; 
}