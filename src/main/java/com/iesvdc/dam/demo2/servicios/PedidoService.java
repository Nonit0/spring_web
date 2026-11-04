package com.iesvdc.dam.demo2.servicios;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.EstadoPedido;
import com.iesvdc.dam.demo2.modelos.LineaPedido;
import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;
import com.iesvdc.dam.demo2.repositorios.RepoPedido;

@Service
public class PedidoService {

    private final RepoPedido repoPedido;
    private final RepoCamiseta repoCamiseta;

    public PedidoService(RepoPedido repoPedido, RepoCamiseta repoCamiseta) {
        this.repoPedido = repoPedido;
        this.repoCamiseta = repoCamiseta;
    }

    /**
     * Crea el pedido y descuenta el stock en una sola transacción: si falta stock de
     * cualquier línea no se guarda nada. Se bloquea en orden de id para evitar interbloqueos.
     */
    @Transactional
    public Pedido crear(Usuario usuario, Map<Long, Integer> lineas) {
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());

        for (Map.Entry<Long, Integer> entrada : new TreeMap<>(lineas).entrySet()) {
            int cantidad = entrada.getValue();
            if (cantidad <= 0) {
                throw new IllegalArgumentException("Cantidad no válida");
            }
            Camiseta camiseta = repoCamiseta.findByIdForUpdate(entrada.getKey())
                    .orElseThrow(() -> new StockInsuficienteException("Una de las camisetas ya no está a la venta"));
            if (camiseta.getStock() < cantidad) {
                throw new StockInsuficienteException(
                        "No hay stock suficiente de «" + camiseta.getNombre() + "»: quedan " + camiseta.getStock());
            }
            camiseta.setStock(camiseta.getStock() - cantidad);

            LineaPedido linea = new LineaPedido();
            linea.setCamisetaId(camiseta.getId());
            linea.setNombre(camiseta.getNombre());
            linea.setTalla(camiseta.getTalla());
            linea.setColor(camiseta.getColor());
            linea.setPrecioUnitario(camiseta.getPrecio());
            linea.setCantidad(cantidad);
            pedido.anadirLinea(linea);
        }
        return repoPedido.save(pedido);
    }

    /** Cambia el estado. Cancelar devuelve el stock, y un pedido cancelado no se reabre. */
    @Transactional
    public void cambiarEstado(Long pedidoId, EstadoPedido nuevo) {
        Pedido pedido = repoPedido.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe"));
        if (pedido.getEstado() == nuevo) {
            return;
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("Un pedido cancelado no se puede reabrir");
        }
        if (nuevo == EstadoPedido.CANCELADO) {
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                throw new IllegalStateException("Un pedido entregado no se puede cancelar");
            }
            new TreeMap<>(cantidadesPorCamiseta(pedido)).forEach((id, cantidad) -> repoCamiseta.findByIdForUpdate(id)
                    .ifPresent(c -> c.setStock(c.getStock() + cantidad)));
        }
        pedido.setEstado(nuevo);
    }

    private Map<Long, Integer> cantidadesPorCamiseta(Pedido pedido) {
        Map<Long, Integer> cantidades = new TreeMap<>();
        pedido.getLineas().forEach(l -> cantidades.merge(l.getCamisetaId(), l.getCantidad(), Integer::sum));
        return cantidades;
    }
}
