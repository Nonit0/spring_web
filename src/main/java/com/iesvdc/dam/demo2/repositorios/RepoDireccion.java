package com.iesvdc.dam.demo2.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.Direccion;

@Repository
public interface RepoDireccion extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuarioId(Long usuarioId);

}
