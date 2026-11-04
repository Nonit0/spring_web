package com.iesvdc.dam.demo2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.iesvdc.dam.demo2.modelos.Camiseta;
import com.iesvdc.dam.demo2.modelos.Pedido;
import com.iesvdc.dam.demo2.repositorios.RepoCamiseta;
import com.iesvdc.dam.demo2.repositorios.RepoPedido;
import com.iesvdc.dam.demo2.repositorios.RepoUsuario;
import com.iesvdc.dam.demo2.servicios.PedidoService;

@SpringBootTest
@AutoConfigureMockMvc
class TiendaWebTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    RepoCamiseta repoCamiseta;
    @Autowired
    RepoUsuario repoUsuario;
    @Autowired
    RepoPedido repoPedido;
    @Autowired
    PedidoService pedidoService;

    private Camiseta conStock() {
        return repoCamiseta.findAll().stream().filter(c -> c.getStock() > 5).findFirst().orElseThrow();
    }

    private Pedido pedidoDe(String username) {
        return pedidoService.crear(repoUsuario.findByUsername(username).orElseThrow(), Map.of(conStock().getId(), 1));
    }

    @Test
    void elCatalogoEsPublicoYFiltra() throws Exception {
        mvc.perform(get("/catalogo")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Camiseta Logo Clásico")));
        mvc.perform(get("/catalogo").param("talla", "XS")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Camiseta Entallada")))
                .andExpect(content().string(not(containsString("Camiseta Logo Clásico"))));
        mvc.perform(get("/catalogo").param("color", "GRIS")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Camiseta Logo Clásico")))
                .andExpect(content().string(not(containsString("Camiseta Entallada"))));
    }

    @Test
    void unFiltroNoValidoNoRompeLaPagina() throws Exception {
        mvc.perform(get("/catalogo").param("talla", "NOEXISTE")).andExpect(status().isBadRequest());
    }

    @Test
    void unaCamisetaInexistenteResponde404() throws Exception {
        mvc.perform(get("/catalogo/999999")).andExpect(status().isNotFound());
        mvc.perform(get("/error").requestAttr(jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE, 404))
                .andExpect(status().isOk());
    }

    @Test
    void lasEscriturasSinTokenCsrfSeRechazan() throws Exception {
        mvc.perform(post("/carrito/add").param("id", "1")).andExpect(status().isForbidden());
        mvc.perform(post("/admin/provincia/save").with(user("admin.demo").authorities(() -> "ADMIN"))
                .param("codigo", "1").param("nombre", "x").param("comunidad", "y"))
                .andExpect(status().isForbidden());
    }

    @Test
    void pagarYVerPedidosExigeIniciarSesion() throws Exception {
        mvc.perform(get("/carrito/pago")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/pedidos")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void elPanelSoloEsParaAdministradores() throws Exception {
        mvc.perform(get("/admin/camiseta")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/camiseta").with(user("ana.demo").authorities(() -> "CLIENTE")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin/pedido").with(user("ana.demo").authorities(() -> "CLIENTE")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin/camiseta").with(user("admin.demo").authorities(() -> "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void unClienteNoVePedidosAjenos() throws Exception {
        Pedido deAna = pedidoDe("ana.demo");
        String ruta = "/pedidos/" + deAna.getId();
        mvc.perform(get(ruta).with(user("ana.demo").authorities(() -> "CLIENTE"))).andExpect(status().isOk());
        mvc.perform(get(ruta).with(user("luis.demo").authorities(() -> "CLIENTE"))).andExpect(status().isNotFound());
        mvc.perform(get(ruta).with(user("admin.demo").authorities(() -> "ADMIN"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("ana.demo")));
    }

    @Test
    void unClienteNoCambiaElEstadoDeUnPedido() throws Exception {
        Pedido pedido = pedidoDe("ana.demo");
        mvc.perform(post("/admin/pedido/" + pedido.getId() + "/estado").with(csrf())
                .with(user("ana.demo").authorities(() -> "CLIENTE")).param("estado", "CANCELADO"))
                .andExpect(status().isForbidden());
    }

    @Test
    void comprarDesdeElCarritoCreaElPedidoYLoVacia() throws Exception {
        Camiseta camiseta = conStock();
        int stock = camiseta.getStock();
        MockHttpSession sesion = new MockHttpSession();
        var ana = user("ana.demo").authorities(() -> "CLIENTE");

        mvc.perform(post("/carrito/add").session(sesion).with(csrf()).param("id", camiseta.getId().toString()).param("cantidad", "2"))
                .andExpect(redirectedUrl("/carrito"));
        mvc.perform(get("/carrito").session(sesion)).andExpect(content().string(containsString(camiseta.getNombre())));
        mvc.perform(get("/carrito/pago").session(sesion).with(ana)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Pago simulado")));
        mvc.perform(post("/carrito/pago").session(sesion).with(csrf()).with(ana))
                .andExpect(redirectedUrlPattern("/pedidos/*"));

        assertThat(repoCamiseta.findById(camiseta.getId()).orElseThrow().getStock()).isEqualTo(stock - 2);
        mvc.perform(get("/carrito/pago").session(sesion).with(ana)).andExpect(redirectedUrl("/carrito"));
    }

    @Test
    void elCarritoNoSuperaElStockNiAdmiteAgotadas() throws Exception {
        Camiseta ultima = repoCamiseta.findAll().stream().filter(c -> c.getStock() == 1).findFirst().orElseThrow();
        Camiseta agotada = repoCamiseta.findAll().stream().filter(c -> c.getStock() == 0).findFirst().orElseThrow();
        MockHttpSession sesion = new MockHttpSession();

        mvc.perform(post("/carrito/add").session(sesion).with(csrf()).param("id", ultima.getId().toString()).param("cantidad", "5"))
                .andExpect(redirectedUrl("/carrito"));
        mvc.perform(get("/carrito").session(sesion)).andExpect(content().string(containsString("<span class=\"nav-count\">1</span>")));
        mvc.perform(post("/carrito/add").session(sesion).with(csrf()).param("id", agotada.getId().toString()))
                .andExpect(redirectedUrl("/catalogo/" + agotada.getId()));
    }

    @Test
    void validaLosDatosAlCrearUnaCamiseta() throws Exception {
        long antes = repoCamiseta.count();
        mvc.perform(post("/admin/camiseta/add").with(csrf()).with(user("admin.demo").authorities(() -> "ADMIN"))
                .param("nombre", "").param("talla", "M").param("color", "ROJO").param("precio", "-1").param("stock", "-2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pon un nombre")))
                .andExpect(content().string(containsString("mayor que cero")));
        assertThat(repoCamiseta.count()).isEqualTo(antes);
    }

    @Test
    void lasPaginasDelPanelSeRenderizan() throws Exception {
        var admin = user("admin.demo").authorities(() -> "ADMIN");
        Camiseta c = conStock();
        pedidoDe("ana.demo");
        for (String ruta : new String[] { "/", "/admin/camiseta/add", "/admin/camiseta/edit/" + c.getId(),
                "/admin/camiseta/del/" + c.getId(), "/admin/pedido", "/admin/provincia", "/admin/usuario" }) {
            mvc.perform(get(ruta).with(admin)).andExpect(status().isOk());
        }
    }
}
