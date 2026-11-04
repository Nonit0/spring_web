package com.iesvdc.dam.demo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.EstadoPedido;
import com.iesvdc.dam.demo2.repositorios.RepoPedido;
import com.iesvdc.dam.demo2.servicios.PedidoService;

@Controller
@RequestMapping("/admin/pedido")
public class PedidoAdminController {

    private final RepoPedido repoPedido;
    private final PedidoService pedidoService;

    public PedidoAdminController(RepoPedido repoPedido, PedidoService pedidoService) {
        this.repoPedido = repoPedido;
        this.pedidoService = pedidoService;
    }

    @GetMapping({ "", "/" })
    public String listar(Model modelo) {
        modelo.addAttribute("titulo", "Pedidos");
        modelo.addAttribute("pedidos", repoPedido.findAllByOrderByFechaDesc());
        modelo.addAttribute("estados", EstadoPedido.values());
        return "pedidoadmin/list";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado,
            RedirectAttributes redirect) {
        try {
            pedidoService.cambiarEstado(id, estado);
            redirect.addFlashAttribute("mensaje", "Pedido " + id + ": " + estado.getEtiqueta());
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedido";
    }
}
