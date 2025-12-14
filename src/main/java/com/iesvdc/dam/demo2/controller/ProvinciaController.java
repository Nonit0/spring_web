package com.iesvdc.dam.demo2.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.iesvdc.dam.demo2.modelos.Provincia;
import com.iesvdc.dam.demo2.repositorios.RepoProvincia;

import io.micrometer.common.lang.NonNull;


@Controller
@RequestMapping("/admin/provincia")
public class ProvinciaController {
    
    @Autowired
    RepoProvincia repoProvincia;

    @GetMapping({"","/"})
    public String findAll(Model modelo) {
        modelo.addAttribute("titulo", "Listado de Provincias");
        modelo.addAttribute("listaProvincias", repoProvincia.findAll());
        return "provincia/list";
    }
    
    @GetMapping("/add")
    public String add(Model modelo) {
        modelo.addAttribute("accion", "añadir");
        modelo.addAttribute("provincia", new Provincia());
        return "provincia/create";
    }

    @PostMapping("/save")
    public String save(
        @ModelAttribute("provincia") Provincia provincia ) {

        repoProvincia.save(provincia);
        return "redirect:.";
    }


    @GetMapping("/edit/{id}")
    public String editForm(
        Model modelo,
        @PathVariable(name = "id") @NonNull Integer codigo ) {

        Optional<Provincia> oProvincia = repoProvincia.findByCodigo(codigo);
        if (oProvincia.isPresent()) {
            modelo.addAttribute("accion", "editar");
            modelo.addAttribute("provincia", oProvincia.get());
            return "provincia/create";
        } else {
            modelo.addAttribute("titulo", "Gestión de Provincias");
            modelo.addAttribute("mensaje", "Esa provincia no existe");
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String edit(
        @ModelAttribute("provincia") Provincia provincia,
        @PathVariable(name = "id") @NonNull Integer codigo ) {

        repoProvincia.save(provincia);
        return "redirect:provincia";
    }

    @GetMapping("/del/{id}")
    public String delForm(
        Model modelo,
        @PathVariable(name = "id") @NonNull Integer codigo ) {

        Optional<Provincia> oProvincia = repoProvincia.findByCodigo(codigo);
        if (oProvincia.isPresent()) {
            modelo.addAttribute("accion", "borrar");
            modelo.addAttribute("provincia", oProvincia.get());
            return "provincia/create";
        } else {
            modelo.addAttribute("titulo", "Gestión de Provincias");
            modelo.addAttribute("mensaje", "Esa provincia no existe");
            return "error";
        }
    }
        
    @PostMapping("/del/{id}")
    public String delByID(
        @ModelAttribute("provincia") Provincia provincia,
        @PathVariable(name = "id") @NonNull Integer codigo ) {

        repoProvincia.deleteById(codigo);;
        return "redirect:..";
    }

}
