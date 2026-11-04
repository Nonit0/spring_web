package com.iesvdc.dam.demo2.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.modelos.Usuario;

@Repository
public interface RepoPedido extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    List<Pedido> findAllByOrderByFechaDesc();
}
