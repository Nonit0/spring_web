package com.iesvdc.dam.demo2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.ColorCamiseta;
import com.iesvdc.dam.demo2.modelos.Talla;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final RepoCamiseta repoCamiseta;

    public CatalogoController(RepoCamiseta repoCamiseta) {
        this.repoCamiseta = repoCamiseta;
    }

    @GetMapping({ "", "/" })
    public String listar(
            @RequestParam(required = false) Talla talla,
            @RequestParam(required = false) ColorCamiseta color,
            Model modelo) {
        modelo.addAttribute("titulo", "Catálogo");
        modelo.addAttribute("camisetas", repoCamiseta.buscar(talla, color));
        modelo.addAttribute("tallas", Talla.values());
        modelo.addAttribute("colores", ColorCamiseta.values());
        modelo.addAttribute("tallaElegida", talla);
        modelo.addAttribute("colorElegido", color);
        return "catalogo/list";
    }

    @GetMapping("/{id}")
    public String ficha(@PathVariable Long id, Model modelo) {
        Camiseta camiseta = repoCamiseta.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        modelo.addAttribute("titulo", camiseta.getNombre());
        modelo.addAttribute("camiseta", camiseta);
        return "catalogo/ficha";
    }
}
