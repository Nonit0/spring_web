package com.iesvdc.dam.demo2.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.RolUsuario;
import com.iesvdc.dam.demo2.modelos.Usuario;

import java.util.List;


@Repository
public interface RepoRolUsuario extends JpaRepository<RolUsuario, Long> {
    List<RolUsuario> findByUsuario(Usuario usuario);

    void deleteByUsuarioId(Long id);
}