package com.iesvdc.dam.demo2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.Rol;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;
import com.iesvdc.dam.demo2.repositorios.RepoRolUsuario;
import com.iesvdc.dam.demo2.repositorios.RepoTelefono;

@Controller
@RequestMapping("/admin/usuario")
public class UsuarioAdminController {

    @Autowired private RepoUsuario repoUsuario;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RepoRolUsuario repoRolUsuario;
    @Autowired private RepoTelefono repoTelefono; 
    
    // ----------------------------------------------------------------------------------
    // 1. LISTAR (GET /admin/usuario/)
    // ----------------------------------------------------------------------------------
    @GetMapping({"/", ""})
    public String listUsuarios(Model model) {
        List<Usuario> usuarios = repoUsuario.findAll();
        model.addAttribute("usuariosList", usuarios);
        // El listado de roles es necesario para el desplegable de edición
        model.addAttribute("roles", Rol.values()); 
        return "usuario/list"; // Vuelve a la vista que ya tienes
    }

    // ----------------------------------------------------------------------------------
    // 2. CREAR (GET y POST)
    // ----------------------------------------------------------------------------------
    @GetMapping("/add")
    public String addUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("accion", "añadir");
        return "usuario/create";
    }

    @PostMapping("/add")
    @Transactional
    public String addUsuario(
        Usuario usuario, 
        // Capturamos el valor del formulario 'rawPassword' y lo hacemos opcional (para evitar errores de binding)
        @RequestParam(value = "rawPassword", required = false) String rawPassword, 
        RedirectAttributes ra) {

        // 1. VALIDACIÓN: Comprobamos si el valor capturado es nulo o vacío (obligatorio para añadir)
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            ra.addFlashAttribute("error", "La contraseña es obligatoria.");
            return "redirect:/admin/usuario/add"; 
        }
        
        try {
            // 2. CIFRADO: Ciframos la contraseña en bruto (rawPassword)
            // y la asignamos a la propiedad 'password' del objeto Usuario.
            usuario.setPassword(passwordEncoder.encode(rawPassword));
            
            // 3. Guardar el usuario (ahora tiene la contraseña cifrada)
            repoUsuario.save(usuario);

            // Nota: Asegúrate de que otros campos obligatorios (como el rol si no viene del formulario)
            // estén asignados antes de guardar si no se bindean automáticamente.

            ra.addFlashAttribute("mensaje", "Usuario " + usuario.getUsername() + " añadido con éxito.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Error: Ya existe un usuario con ese username, email o NIF.");
            return "redirect:/admin/usuario/add";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/admin/usuario/add"; // Redireccionar al formulario en caso de error
        }
        
        return "redirect:/admin/usuario/";
    }

    // ----------------------------------------------------------------------------------
    // 3. EDITAR (GET y POST)
    // ----------------------------------------------------------------------------------
    @GetMapping("/edit/{id}")
    public String editUsuarioForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = repoUsuario.findById(id);
        if (optionalUsuario.isEmpty()) {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuario/";
        }

        model.addAttribute("usuario", optionalUsuario.get());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("accion", "editar");
        return "usuario/create";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String editUsuario(@PathVariable Long id, Usuario formUsuario, RedirectAttributes ra) {
        try {
            Usuario existingUsuario = repoUsuario.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de usuario inválido:" + id));

            // Actualizar campos
            existingUsuario.setUsername(formUsuario.getUsername());
            existingUsuario.setEmail(formUsuario.getEmail());
            existingUsuario.setNif(formUsuario.getNif());
            existingUsuario.setTelefono(formUsuario.getTelefono());
            existingUsuario.setRol(formUsuario.getRol());
            existingUsuario.setEnabled(formUsuario.isEnabled());

            // Si se proporciona nueva contraseña, cifrar y actualizar
            if (formUsuario.getRawPassword() != null && !formUsuario.getRawPassword().isEmpty()) {
                existingUsuario.setPassword(passwordEncoder.encode(formUsuario.getRawPassword()));
            }

            repoUsuario.save(existingUsuario);
            
            // Actualizar entidades relacionadas (RolUsuario, Telefono) si fuera necesario.

            ra.addFlashAttribute("mensaje", "Usuario '" + existingUsuario.getUsername() + "' actualizado con éxito.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Error: Ya existe un usuario con ese username, email o NIF.");
            return "redirect:/admin/usuario/edit/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
            return "redirect:/admin/usuario/";
        }
        return "redirect:/admin/usuario/";
    }

    // ----------------------------------------------------------------------------------
    // 4. BORRAR (GET y POST)
    // ----------------------------------------------------------------------------------
    @GetMapping("/del/{id}")
    public String delUsuarioForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Usuario> optionalUsuario = repoUsuario.findById(id);
        if (optionalUsuario.isEmpty()) {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuario/";
        }
        model.addAttribute("usuario", optionalUsuario.get());
        return "usuario/delete-confirm";
    }

    @PostMapping("/del/{id}")
    @Transactional
    public String delUsuario(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // Eliminar entidades dependientes primero (Telefono y RolUsuario)
            repoTelefono.deleteByUsuarioId(id);
            repoRolUsuario.deleteByUsuarioId(id);
            
            // Eliminar Direcciones (si usáramos el @OneToMany, JPA lo haría,
            // pero con el mapeo simple, deberías usar repoDireccion.deleteByUsuarioId(id) si existiera)
            // Por ahora, asumimos que no hay problema de integridad referencial o se borran en cascada.
            
            repoUsuario.deleteById(id);
            ra.addFlashAttribute("mensaje", "Usuario " + id + " eliminado con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuario/";
    }
}