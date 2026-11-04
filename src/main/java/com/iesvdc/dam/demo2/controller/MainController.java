package com.iesvdc.dam.demo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iesvdc.dam.demo2.modelos.ColorCamiseta;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;

@Controller
public class MainController {

    private final RepoCamiseta repoCamiseta;

    public MainController(RepoCamiseta repoCamiseta) {
        this.repoCamiseta = repoCamiseta;
    }

    @GetMapping("")
    public String index(Model modelo) {
        modelo.addAttribute("destacadas", repoCamiseta.findTop4ByStockGreaterThanOrderByPrecioDesc(0));
        modelo.addAttribute("colorPortada", ColorCamiseta.MORADO);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Acepta cualquier método: Spring Security reenvía aquí también los POST rechazados. */
    @RequestMapping("/denegado")
    public String denegado() {
        return "denegado";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
