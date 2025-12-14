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

import com.iesvdc.dam.demo2.modelos.CodPos;
import com.iesvdc.dam.demo2.modelos.Provincia;
import com.iesvdc.dam.demo2.repositorios.RepoCodPos;
import com.iesvdc.dam.demo2.repositorios.RepoProvincia;

import io.micrometer.common.lang.NonNull;


@Controller
@RequestMapping("/admin/codPos")
public class CodPosController {
    
    @Autowired
    RepoCodPos repoCodPos;

    @Autowired
    RepoProvincia repoProvincia; // Repositorio de provincias para llenar el desplegable

    // 1. LISTAR TODOS (URL: /admin/codPos)
    @GetMapping({"","/"})
    public String findAll(Model modelo) {
        modelo.addAttribute("titulo", "Listado de Códigos Postales");
        modelo.addAttribute("listaCodPos", repoCodPos.findAll());
        return "codPos/list";
    }
    
    // 2. FORMULARIO AÑADIR (URL: /admin/codPos/add)
    @GetMapping("/add")
    public String add(Model modelo) {
        modelo.addAttribute("accion", "añadir");
        modelo.addAttribute("codPos", new CodPos());
        modelo.addAttribute("provinciasList", repoProvincia.findAll());
        return "codPos/create";
    }

    // 3. GUARDAR (AÑADIR O EDITAR) (URL: /admin/codPos/save)
    @PostMapping("/add")
    public String addCodPos(@ModelAttribute("codPos") CodPos codPos ) {

        // --- SOLUCIÓN A LA RELACIÓN (MISMA LÓGICA QUE UN MÉTODO SAVE) ---
        Integer idProvincia = codPos.getProvinciaId();
        if (idProvincia != null) {
            Optional<Provincia> oProvincia = repoProvincia.findById(idProvincia);
            if (oProvincia.isPresent()) {
                codPos.setProvincia(oProvincia.get());
            } else {
                throw new IllegalArgumentException("El ID de provincia proporcionado no es válido: " + idProvincia);
            }
        }
        // ------------------------------------------------------------------

        repoCodPos.save(codPos);
        return "redirect:."; // Redirige a /admin/codpos/
    }

    
    // 4. FORMULARIO EDITAR (GET /admin/codPos/edit/{cp})
    @GetMapping("/edit/{cp}")
    public String editCodPosForm(
        Model modelo,
        @PathVariable(name = "cp") @NonNull Integer cp ) {

        modelo.addAttribute("provinciasList", repoProvincia.findAll());

        Optional<CodPos> oCodPos = repoCodPos.findByCp(cp);
        if (oCodPos.isPresent()) {
            modelo.addAttribute("accion", "editar");
            modelo.addAttribute("codPos", oCodPos.get());
            return "codPos/create";
        } else {
            modelo.addAttribute("titulo", "Gestión de Códigos Postales");
            modelo.addAttribute("mensaje", "Ese Código Postal no existe");
            return "error";
        }
    }

    // revisar id que es Integer!
    // 5. POST EDITAR
    @PostMapping("/edit/{cp}")
    public String editCodPos(
        @ModelAttribute("codPos") CodPos codPos,
        @PathVariable(name = "cp") @NonNull Integer cp ) { 
        
        // --- SOLUCIÓN A LA RELACIÓN (MISMA LÓGICA QUE UN MÉTODO SAVE) ---
        Integer idProvincia = codPos.getProvinciaId();
        if (idProvincia != null) {
            Optional<Provincia> oProvincia = repoProvincia.findById(idProvincia);
            if (oProvincia.isPresent()) {
                codPos.setProvincia(oProvincia.get());
            } else {
                throw new IllegalArgumentException("El ID de provincia proporcionado no es válido: " + idProvincia);
            }
        }
        // ------------------------------------------------------------------

        repoCodPos.save(codPos);
        return "redirect:.."; // Redirige a /admin/codpos/
    }

    // 6. FORMULARIO BORRAR (GET /admin/codPos/del/{cp})
    @GetMapping("/del/{cp}")
    public String delCodPosForm(
        Model modelo,
        @PathVariable(name = "cp") @NonNull Integer cp ) {

        Optional<CodPos> oCodPos = repoCodPos.findByCp(cp);
        if (oCodPos.isPresent()) {
            modelo.addAttribute("accion", "borrar");
            modelo.addAttribute("codPos", oCodPos.get());
            // Nota: vista específica de confirmación para borrar
            return "codPos/delete-confirm";
        } else {
            modelo.addAttribute("titulo", "Gestión de Código de Provincias");
            modelo.addAttribute("mensaje", "Ese Código no existe");
            return "error";
        }
    }
    
    // 7. EJECUTAR BORRADO (POST /admin/codPos/del/{cp})
@PostMapping("/del/{cp}")
    public String delete(
        @PathVariable(name = "cp") @NonNull Integer cp ) { 

        // Ejecuta el borrado usando el ID (cp)
        repoCodPos.deleteById(cp); 
        
        return "redirect:../"; // Redirige a /admin/codPos/
    }

}
