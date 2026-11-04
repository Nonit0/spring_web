package com.iesvdc.dam.demo2.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoPedido;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final RepoPedido repoPedido;
    private final RepoUsuario repoUsuario;

    public PedidoController(RepoPedido repoPedido, RepoUsuario repoUsuario) {
        this.repoPedido = repoPedido;
        this.repoUsuario = repoUsuario;
    }

    @GetMapping({ "", "/" })
    public String misPedidos(Principal principal, Model modelo) {
        Usuario usuario = repoUsuario.findByUsername(principal.getName()).orElseThrow();
        modelo.addAttribute("titulo", "Mis pedidos");
        modelo.addAttribute("pedidos", repoPedido.findByUsuarioOrderByFechaDesc(usuario));
        return "pedido/list";
    }

    /** Un cliente solo ve sus pedidos: los ajenos responden como si no existieran. */
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Authentication auth, Model modelo) {
        Pedido pedido = repoPedido.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!esAdmin && !pedido.getUsuario().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        modelo.addAttribute("titulo", "Pedido " + pedido.getId());
        modelo.addAttribute("pedido", pedido);
        return "pedido/detalle";
    }
}
