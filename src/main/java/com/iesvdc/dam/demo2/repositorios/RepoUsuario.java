package com.iesvdc.dam.demo2.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.Usuario;
import java.util.Optional;


@Repository
public interface RepoUsuario extends JpaRepository<Usuario, Long>{

    Optional<Usuario>  findByUsername(String username);
    Optional<Usuario>  findByEmail(String email);
    Optional<Usuario>  findByTelefono(int telefono);
    
}
