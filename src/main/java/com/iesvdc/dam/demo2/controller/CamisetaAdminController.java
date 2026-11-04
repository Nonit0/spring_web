package com.iesvdc.dam.demo2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.ColorCamiseta;
import com.iesvdc.dam.demo2.modelos.Talla;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/camiseta")
public class CamisetaAdminController {

    private final RepoCamiseta repoCamiseta;

    public CamisetaAdminController(RepoCamiseta repoCamiseta) {
        this.repoCamiseta = repoCamiseta;
    }

    @ModelAttribute("tallas")
    public Talla[] tallas() {
        return Talla.values();
    }

    @ModelAttribute("colores")
    public ColorCamiseta[] colores() {
        return ColorCamiseta.values();
    }

    private Camiseta buscar(Long id) {
        return repoCamiseta.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping({ "", "/" })
    public String listar(Model modelo) {
        modelo.addAttribute("titulo", "Camisetas");
        modelo.addAttribute("camisetas", repoCamiseta.findAll());
        return "camiseta/list";
    }

    @GetMapping("/add")
    public String nueva(Model modelo) {
        modelo.addAttribute("titulo", "Nueva camiseta");
        modelo.addAttribute("accion", "añadir");
        modelo.addAttribute("camiseta", new Camiseta());
        return "camiseta/create";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("titulo", "Editar camiseta");
        modelo.addAttribute("accion", "editar");
        modelo.addAttribute("camiseta", buscar(id));
        return "camiseta/create";
    }

    @PostMapping("/add")
    public String crear(@Valid @ModelAttribute("camiseta") Camiseta camiseta, BindingResult errores,
            Model modelo, RedirectAttributes redirect) {
        if (errores.hasErrors()) {
            modelo.addAttribute("accion", "añadir");
            return "camiseta/create";
        }
        camiseta.setId(null);
        repoCamiseta.save(camiseta);
        redirect.addFlashAttribute("mensaje", "Camiseta creada");
        return "redirect:/admin/camiseta";
    }

    @PostMapping("/edit/{id}")
    public String guardar(@PathVariable Long id, @Valid @ModelAttribute("camiseta") Camiseta camiseta,
            BindingResult errores, Model modelo, RedirectAttributes redirect) {
        buscar(id);
        if (errores.hasErrors()) {
            modelo.addAttribute("accion", "editar");
            return "camiseta/create";
        }
        camiseta.setId(id);
        repoCamiseta.save(camiseta);
        redirect.addFlashAttribute("mensaje", "Camiseta actualizada");
        return "redirect:/admin/camiseta";
    }

    @GetMapping("/del/{id}")
    public String confirmarBorrado(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("titulo", "Borrar camiseta");
        modelo.addAttribute("camiseta", buscar(id));
        return "camiseta/delete-confirm";
    }

    @PostMapping("/del/{id}")
    public String borrar(@PathVariable Long id, RedirectAttributes redirect) {
        repoCamiseta.delete(buscar(id));
        redirect.addFlashAttribute("mensaje", "Camiseta borrada. Los pedidos antiguos conservan sus datos.");
        return "redirect:/admin/camiseta";
    }
}
