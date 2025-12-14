package com.iesvdc.dam.demo2.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.iesvdc.dam.demo2.modelos.CodPos;
import com.iesvdc.dam.demo2.modelos.Provincia;


@Repository
public interface RepoCodPos extends JpaRepository<CodPos, Integer>{

    Optional<CodPos> findByCp(Integer cp);
    List<Provincia> findByProvincia(Provincia provincia);
}
