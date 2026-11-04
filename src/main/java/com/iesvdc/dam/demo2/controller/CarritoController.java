package com.iesvdc.dam.demo2.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;
import com.iesvdc.dam.demo2.servicios.PedidoService;
import com.iesvdc.dam.demo2.servicios.StockInsuficienteException;
import com.iesvdc.dam.demo2.tienda.Carrito;
import com.iesvdc.dam.demo2.tienda.LineaCarrito;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final Carrito carrito;
    private final RepoCamiseta repoCamiseta;
    private final RepoUsuario repoUsuario;
    private final PedidoService pedidoService;

    public CarritoController(Carrito carrito, RepoCamiseta repoCamiseta, RepoUsuario repoUsuario,
            PedidoService pedidoService) {
        this.carrito = carrito;
        this.repoCamiseta = repoCamiseta;
        this.repoUsuario = repoUsuario;
        this.pedidoService = pedidoService;
    }

    private List<LineaCarrito> lineas() {
        return carrito.getLineas().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .flatMap(e -> repoCamiseta.findById(e.getKey())
                        .map(c -> new LineaCarrito(c, e.getValue())).stream())
                .toList();
    }

    private void prepararResumen(Model modelo) {
        List<LineaCarrito> lineas = lineas();
        modelo.addAttribute("lineas", lineas);
        modelo.addAttribute("total", lineas.stream().map(LineaCarrito::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        modelo.addAttribute("faltaStock", lineas.stream().anyMatch(LineaCarrito::sinStockSuficiente));
    }

    @GetMapping({ "", "/" })
    public String ver(Model modelo) {
        modelo.addAttribute("titulo", "Carrito");
        prepararResumen(modelo);
        return "carrito/ver";
    }

    @PostMapping("/add")
    public String anadir(@RequestParam Long id, @RequestParam(defaultValue = "1") int cantidad,
            RedirectAttributes redirect) {
        Camiseta camiseta = repoCamiseta.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        int deseada = carrito.cantidadDe(id) + Math.max(cantidad, 1);
        if (camiseta.isAgotada()) {
            redirect.addFlashAttribute("error", "«" + camiseta.getNombre() + "» está agotada");
            return "redirect:/catalogo/" + id;
        }
        carrito.fijar(id, Math.min(deseada, camiseta.getStock()));
        redirect.addFlashAttribute(deseada > camiseta.getStock() ? "error" : "mensaje",
                deseada > camiseta.getStock()
                        ? "Solo quedan " + camiseta.getStock() + " unidades de «" + camiseta.getNombre() + "»"
                        : "«" + camiseta.getNombre() + "» añadida al carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/update")
    public String actualizar(@RequestParam Long id, @RequestParam int cantidad, RedirectAttributes redirect) {
        int stock = repoCamiseta.findById(id).map(Camiseta::getStock).orElse(0);
        carrito.fijar(id, Math.min(cantidad, stock));
        if (cantidad > stock) {
            redirect.addFlashAttribute("error", "Solo quedan " + stock + " unidades");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/remove")
    public String quitar(@RequestParam Long id) {
        carrito.quitar(id);
        return "redirect:/carrito";
    }

    @GetMapping("/pago")
    public String pago(Model modelo) {
        if (carrito.getUnidades() == 0) {
            return "redirect:/carrito";
        }
        modelo.addAttribute("titulo", "Confirmar pedido");
        prepararResumen(modelo);
        return "carrito/pago";
    }

    @PostMapping("/pago")
    public String confirmar(Principal principal, RedirectAttributes redirect) {
        if (carrito.getUnidades() == 0) {
            return "redirect:/carrito";
        }
        Usuario usuario = repoUsuario.findByUsername(principal.getName()).orElseThrow();
        try {
            Pedido pedido = pedidoService.crear(usuario, carrito.getLineas());
            carrito.vaciar();
            redirect.addFlashAttribute("mensaje", "Pedido confirmado. No se ha cobrado nada: el pago es simulado.");
            return "redirect:/pedidos/" + pedido.getId();
        } catch (StockInsuficienteException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }
}
