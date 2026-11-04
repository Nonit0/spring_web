package com.iesvdc.dam.demo2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.ColorCamiseta;
import com.iesvdc.dam.demo2.modelos.EstadoPedido;
import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.modelos.Talla;
import com.iesvdc.dam.demo2.modelos.Usuario;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;
import com.iesvdc.dam.demo2.repositorios.RepoPedido;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;
import com.iesvdc.dam.demo2.servicios.PedidoService;
import com.iesvdc.dam.demo2.servicios.StockInsuficienteException;

@SpringBootTest
class PedidoServiceTest {

    @Autowired
    PedidoService servicio;
    @Autowired
    RepoCamiseta repoCamiseta;
    @Autowired
    RepoPedido repoPedido;
    @Autowired
    RepoUsuario repoUsuario;

    Usuario ana;
    Camiseta barata;
    Camiseta cara;

    private Camiseta camiseta(String nombre, String precio, int stock) {
        Camiseta c = new Camiseta();
        c.setNombre(nombre);
        c.setTalla(Talla.M);
        c.setColor(ColorCamiseta.ROJO);
        c.setPrecio(new BigDecimal(precio));
        c.setStock(stock);
        return repoCamiseta.save(c);
    }

    private int stock(Camiseta c) {
        return repoCamiseta.findById(c.getId()).orElseThrow().getStock();
    }

    @BeforeEach
    void preparar() {
        ana = repoUsuario.findByUsername("ana.demo").orElseThrow();
        barata = camiseta("Barata", "10.50", 5);
        cara = camiseta("Cara", "20.00", 2);
    }

    @Test
    void crearDescuentaStockYCalculaElTotal() {
        Pedido pedido = servicio.crear(ana, Map.of(barata.getId(), 2, cara.getId(), 1));
        assertThat(pedido.getTotal()).isEqualByComparingTo("41.00");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(stock(barata)).isEqualTo(3);
        assertThat(stock(cara)).isEqualTo(1);
    }

    @Test
    void siFaltaStockDeUnaLineaNoSeGuardaNada() {
        long pedidos = repoPedido.count();
        assertThatThrownBy(() -> servicio.crear(ana, Map.of(barata.getId(), 2, cara.getId(), 3)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Cara");
        assertThat(stock(barata)).isEqualTo(5);
        assertThat(stock(cara)).isEqualTo(2);
        assertThat(repoPedido.count()).isEqualTo(pedidos);
    }

    @Test
    void rechazaCarritoVacioYCantidadesNoValidas() {
        assertThatThrownBy(() -> servicio.crear(ana, Map.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> servicio.crear(ana, Map.of(barata.getId(), -1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(stock(barata)).isEqualTo(5);
    }

    @Test
    void cancelarDevuelveElStockUnaSolaVez() {
        Pedido pedido = servicio.crear(ana, Map.of(barata.getId(), 2));
        servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO);
        assertThat(stock(barata)).isEqualTo(5);
        servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO);
        assertThat(stock(barata)).isEqualTo(5);
    }

    @Test
    void unPedidoCanceladoNoSeReabreYUnoEntregadoNoSeCancela() {
        Pedido cancelado = servicio.crear(ana, Map.of(barata.getId(), 1));
        servicio.cambiarEstado(cancelado.getId(), EstadoPedido.CANCELADO);
        assertThatThrownBy(() -> servicio.cambiarEstado(cancelado.getId(), EstadoPedido.ENVIADO))
                .isInstanceOf(IllegalStateException.class);

        Pedido entregado = servicio.crear(ana, Map.of(barata.getId(), 1));
        servicio.cambiarEstado(entregado.getId(), EstadoPedido.ENTREGADO);
        assertThatThrownBy(() -> servicio.cambiarEstado(entregado.getId(), EstadoPedido.CANCELADO))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void elPedidoConservaSusDatosAunqueSeEditeOBorreLaCamiseta() {
        Pedido pedido = servicio.crear(ana, Map.of(cara.getId(), 1));
        Camiseta editada = repoCamiseta.findById(cara.getId()).orElseThrow();
        editada.setPrecio(new BigDecimal("99.00"));
        repoCamiseta.save(editada);
        repoCamiseta.deleteById(cara.getId());

        assertThat(repoPedido.findById(pedido.getId()).orElseThrow().getTotal()).isEqualByComparingTo("20.00");
        servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO);
        assertThat(repoPedido.findById(pedido.getId()).orElseThrow().getEstado()).isEqualTo(EstadoPedido.CANCELADO);
    }
}
