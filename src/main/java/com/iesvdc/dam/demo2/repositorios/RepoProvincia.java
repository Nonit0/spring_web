package com.iesvdc.dam.demo2.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iesvdc.dam.demo2.modelos.Provincia;
import java.util.List;
import java.util.Optional;


public interface RepoProvincia extends JpaRepository<Provincia, Integer> {
    List<Provincia> findByComunidad(String comunidad);
    Optional<Provincia> findByCodigo(Integer codigo);
}
