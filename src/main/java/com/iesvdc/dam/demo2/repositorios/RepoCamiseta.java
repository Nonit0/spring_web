package com.iesvdc.dam.demo2.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.ColorCamiseta;
import com.iesvdc.dam.demo2.modelos.Talla;

import jakarta.persistence.LockModeType;

@Repository
public interface RepoCamiseta extends JpaRepository<Camiseta, Long> {

    @Query("""
            select c from Camiseta c
            where (:talla is null or c.talla = :talla)
              and (:color is null or c.color = :color)
            order by case when c.stock = 0 then 1 else 0 end, c.nombre
            """)
    List<Camiseta> buscar(@Param("talla") Talla talla, @Param("color") ColorCamiseta color);

    /** Bloquea la fila para que dos compras simultáneas no vendan el mismo stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Camiseta c where c.id = :id")
    Optional<Camiseta> findByIdForUpdate(@Param("id") Long id);

    List<Camiseta> findTop4ByStockGreaterThanOrderByPrecioDesc(int stock);
}
