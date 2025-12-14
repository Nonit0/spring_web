package com.iesvdc.dam.demo2.modelos;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Provincia {
    @Id
    private Integer codigo;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(nullable = false, length = 50)
    private String comunidad;

    @OneToMany(mappedBy="provincia")
    private List<CodPos> codpos;
}
