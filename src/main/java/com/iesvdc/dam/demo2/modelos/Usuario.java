package com.iesvdc.dam.demo2.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient; // Importante
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Lo mantengo como Long, pero el controlador debe usar Long.

    @Column(unique = true, nullable = false, length = 25)
    private String username;
    
    @Column(nullable = false, length = 64)
    private String password;
    
    @Column(unique = true, nullable = false, length = 120)
    private String email;
    
    @Column(nullable = false) 
    private int telefono; 
    
    @Column(nullable = false, length = 10)    
    private String nif;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    @Column(nullable = false)
    private boolean enabled = true; // Valor por defecto

    // CAMPO TRANSIENT CLAVE PARA LA CONTRASEÑA
    @Transient 
    private String rawPassword;

    
    // @OneToMany(mappedBy = "usuario") private List<Direccion> direcciones;
    // @OneToMany(mappedBy = "usuario") private List<Telefono> telefonos;
    // @OneToMany(mappedBy = "usuario") private List<RolUsuario> roles; 
    // Si necesitas estas entidades, deben gestionarse en sus propios CRUDs.
}