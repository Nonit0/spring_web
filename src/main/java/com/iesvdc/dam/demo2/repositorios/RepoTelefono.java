package com.iesvdc.dam.demo2.repositorios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.Telefono;
import com.iesvdc.dam.demo2.modelos.Usuario;

import java.util.List;


@Repository
public interface RepoTelefono extends JpaRepository<Telefono, Long> {

    List<Telefono> findByUsuario(Usuario usuario);

    void deleteByUsuarioId(Long id);
}