package com.iesvdc.dam.demo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.Rol;
import com.iesvdc.dam.demo2.modelos.RolUsuario;
import com.iesvdc.dam.demo2.modelos.Telefono;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoRolUsuario;
import com.iesvdc.dam.demo2.repositorios.RepoTelefono;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;



@Controller
public class UsuarioController {

    @Autowired
    private RepoTelefono repoTelefono;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepoRolUsuario repoRolUsuario;

    
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @Transactional
    @PostMapping("/register")
    public String register(
        RedirectAttributes ra,
        Model modelo, 
        @NonNull @RequestParam String username,
        @NonNull @RequestParam String password,
        @NonNull @RequestParam String email,
        @NonNull @RequestParam Integer telefono,
        @NonNull @RequestParam String nif) {        

        Usuario usuario = new Usuario();

        try {
            // 1. Configurar y cifrar la contraseña del Usuario
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setEmail(email);
            usuario.setTelefono(telefono); // Usa el campo directo (int)
            usuario.setNif(nif);
            usuario.setEnabled(true);
            usuario.setRol(Rol.CLIENTE); // Usa el campo directo (Enum)
            
            // 2. Guardar el Usuario (puede fallar por unicidad)
            // Spring devolverá la entidad con el ID generado.
            usuario = repoUsuario.save(usuario);
            
            // 3. Crear y guardar el Telefono relacionado
            Telefono tel = new Telefono();
            tel.setNumero(telefono);
            tel.setUsuario(usuario);
            repoTelefono.save(tel);

            // 4. Crear y guardar el RolUsuario relacionado
            RolUsuario rolUsuario = new RolUsuario();
            rolUsuario.setRol(Rol.CLIENTE);
            rolUsuario.setUsuario(usuario);
            repoRolUsuario.save(rolUsuario);

            // Éxito
            modelo.addAttribute("titulo", "Alta nuevo usuario");
            modelo.addAttribute("mensaje", "El usuario "+username+" ha sido creado correctamente.");
            
            // Redirigir al login o a la página principal
            return "redirect:/login"; // Redirigir a una página de éxito

        } // 5. Manejo específico de error de unicidad (username o email duplicado)
        catch (DataIntegrityViolationException e){
            // Error de BD (username o email duplicado, o fallo de nullable=false)
            ra.addFlashAttribute("error", "Error: El username, email o NIF ya existen, o faltan datos obligatorios.");
            return "redirect:/register";
            // e.printStackTrace(); // Útil para depuración
        }
        // 6. Manejo de cualquier otro error (ej. fallo de conexión a BD, fallo al guardar RolUsuario/Telefono)
        catch (Exception e){
            // Otro error:
            ra.addFlashAttribute("error", "Error inesperado al crear el usuario. Inténtalo de nuevo.");
            return "redirect:/register";
            // e.printStackTrace(); // Útil para depuración
        }
    }
}