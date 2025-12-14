package com.iesvdc.dam.demo2.controller;

import java.util.Arrays; // Para la lista de CP
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.Direccion;
import com.iesvdc.dam.demo2.modelos.Direccion.TipoVia;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoDireccion;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;

@Controller
public class DireccionController {

    @Autowired private RepoDireccion repoDireccion;
    @Autowired private RepoUsuario repoUsuario;
    // RepoCodPosCP ya no es necesario

    // Lista simple de CPs para el desplegable (simulando datos de BD)
    private final List<String> codigosPostales = Arrays.asList(
        "23001 - Jaén", "23002 - Jaén", "28001 - Madrid", "41001 - Sevilla", "08001 - Barcelona");

    // Helper para verificar el usuario
    private Optional<Usuario> getUsuario(Long idUsuario, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = repoUsuario.findById(idUsuario);
        if (optionalUsuario.isEmpty()) {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
        }
        return optionalUsuario;
    }

    // 1. GET /admin/usuario/{idUsuario}/direccion
    @GetMapping("/admin/usuario/{idUsuario}/direccion")
    public String findAllByUsuario(@PathVariable("idUsuario") Long idUsuario, Model model, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }

        Usuario usuario = optionalUsuario.get();
        List<Direccion> direcciones = repoDireccion.findByUsuarioId(idUsuario); // Busca por usuarioId (Long)

        model.addAttribute("usuario", usuario);
        model.addAttribute("direccionesList", direcciones);
        return "direccion/list";
    }

    // 2. GET /admin/usuario/{idUsuario}/direccion/add
    @GetMapping("/admin/usuario/{idUsuario}/direccion/add")
    public String addDireccionForm(@PathVariable("idUsuario") Long idUsuario, Model model, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }
        
        Direccion nuevaDireccion = new Direccion();
        nuevaDireccion.setUsuarioId(idUsuario); // Asignar el ID del maestro
        
        model.addAttribute("direccion", nuevaDireccion);
        model.addAttribute("usuario", optionalUsuario.get());
        model.addAttribute("tiposVia", TipoVia.values());
        model.addAttribute("codigosPostales", codigosPostales); // Usamos la lista simple de Strings
        model.addAttribute("accion", "añadir");
        return "direccion/create";
    }

    // 3. POST /admin/usuario/{idUsuario}/direccion/add
    @Transactional
    @PostMapping("/admin/usuario/{idUsuario}/direccion/add")
    public String addDireccion(@PathVariable("idUsuario") Long idUsuario, Direccion direccion, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }
        
        // 1. Forzar el ID del maestro
        direccion.setUsuarioId(idUsuario);
        
        try {
            repoDireccion.save(direccion);
            ra.addFlashAttribute("mensaje", "Dirección creada con éxito para el usuario " + optionalUsuario.get().getUsername());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Error: Revise que todos los campos obligatorios estén rellenos.");
            return "redirect:/admin/usuario/" + idUsuario + "/direccion/add";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
        }
        
        return "redirect:/admin/usuario/" + idUsuario + "/direccion";
    }

    // 4. GET /admin/usuario/{idUsuario}/direccion/edit/{idDireccion}
    @GetMapping("/admin/usuario/{idUsuario}/direccion/edit/{idDireccion}")
    public String editDireccionForm(@PathVariable("idUsuario") Long idUsuario, @PathVariable("idDireccion") Long idDireccion, Model model, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }
        
        Optional<Direccion> optionalDireccion = repoDireccion.findById(idDireccion);
        // Validar que la dirección exista Y pertenezca al usuario
        if (optionalDireccion.isEmpty() || !optionalDireccion.get().getUsuarioId().equals(idUsuario)) {
            ra.addFlashAttribute("error", "Dirección no encontrada o no pertenece a este usuario.");
            return "redirect:/admin/usuario/" + idUsuario + "/direccion";
        }

        model.addAttribute("direccion", optionalDireccion.get());
        model.addAttribute("usuario", optionalUsuario.get());
        model.addAttribute("tiposVia", TipoVia.values());
        model.addAttribute("codigosPostales", codigosPostales);
        model.addAttribute("accion", "editar");
        return "direccion/create";
    }

    // 5. POST /admin/usuario/{idUsuario}/direccion/edit/{idDireccion}
    @Transactional
    @PostMapping("/admin/usuario/{idUsuario}/direccion/edit/{idDireccion}")
    public String editDireccion(@PathVariable("idUsuario") Long idUsuario, @PathVariable("idDireccion") Long idDireccion, Direccion formDireccion, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }

        if (formDireccion.getId() == null || !formDireccion.getId().equals(idDireccion)) {
            ra.addFlashAttribute("error", "Error de formulario: ID de dirección no coincide.");
            return "redirect:/admin/usuario/" + idUsuario + "/direccion/edit/" + idDireccion;
        }

        try {
            // El usuario no se toma del formulario, se fuerza desde el PathVariable
            formDireccion.setUsuarioId(idUsuario); 
            repoDireccion.save(formDireccion);
            ra.addFlashAttribute("mensaje", "Dirección actualizada con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la dirección: " + e.getMessage());
            return "redirect:/admin/usuario/" + idUsuario + "/direccion/edit/" + idDireccion;
        }

        return "redirect:/admin/usuario/" + idUsuario + "/direccion";
    }

    // 6. GET /admin/usuario/{idUsuario}/direccion/del/{idDireccion}
    // Acción: Mostrar formulario de confirmación de borrado.
    @GetMapping("/admin/usuario/{idUsuario}/direccion/del/{idDireccion}")
    public String delDireccionForm(@PathVariable("idUsuario") Long idUsuario, @PathVariable("idDireccion") Long idDireccion, Model model, RedirectAttributes ra) {
        
        // 1. Verificar la existencia del usuario maestro
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }

        // 2. Buscar la dirección a borrar
        Optional<Direccion> optionalDireccion = repoDireccion.findById(idDireccion);
        
        // 3. Validar existencia y pertenencia al usuario
        if (optionalDireccion.isEmpty() || !optionalDireccion.get().getUsuarioId().equals(idUsuario)) {
            ra.addFlashAttribute("error", "Dirección no encontrada o no pertenece a este usuario.");
            return "redirect:/admin/usuario/" + idUsuario + "/direccion";
        }

        model.addAttribute("direccion", optionalDireccion.get());
        model.addAttribute("usuario", optionalUsuario.get()); // Necesario para mostrar el nombre del maestro en la vista de confirmación
        return "direccion/delete-confirm";
    }
    
    // 7. POST /admin/usuario/{idUsuario}/direccion/del/{idDireccion}
    @Transactional
    @PostMapping("/admin/usuario/{idUsuario}/direccion/del/{idDireccion}")
    public String delDireccion(@PathVariable("idUsuario") Long idUsuario, @PathVariable("idDireccion") Long idDireccion, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = getUsuario(idUsuario, ra);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/admin/usuario/";
        }

        try {
            Direccion direccion = repoDireccion.findById(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("ID de dirección inválido."));

            if (!direccion.getUsuarioId().equals(idUsuario)) { // Comprobación con el Long
                throw new IllegalAccessException("La dirección no pertenece a este usuario.");
            }

            repoDireccion.delete(direccion);
            ra.addFlashAttribute("mensaje", "Dirección eliminada con éxito.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la dirección: " + e.getMessage());
        }

        return "redirect:/admin/usuario/" + idUsuario + "/direccion";
    }
}