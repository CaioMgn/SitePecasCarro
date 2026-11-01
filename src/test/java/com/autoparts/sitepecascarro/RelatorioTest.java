package com.autoparts.sitepecascarro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.autoparts.sitepecascarro.entity.ItemPedido;
import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.StatusPedido;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.repository.PedidoRepository;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import com.autoparts.sitepecascarro.service.RelatorioService;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelatorioTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void adminVeATelaDeRelatorios() throws Exception {
        mockMvc.perform(get("/admin/relatorios").with(user("admin@autoparts.com").roles("ADMIN")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Vendas por mês")))
               .andExpect(content().string(containsString("Estoque baixo")));
    }

    @Test
    void clienteNaoAcessaRelatorios() throws Exception {
        mockMvc.perform(get("/admin/relatorios").with(user("cliente@autoparts.com").roles("USER")))
               .andExpect(status().isForbidden());
    }

    @Test
    void relatoriosSomamOsPedidos() {
        Peca peca = new Peca("Peça do relatório", "Suspensão", 50.0, "Peça criada para o teste");
        peca.setMarca("Teste");
        peca.setCompatibilidade("Carro de teste");
        peca.setEstoque(2);
        peca.setImagem("/imagens/pecas/teste.png");
        peca = pecaRepository.save(peca);

        Usuario cliente = usuarioRepository.findByEmail("cliente@autoparts.com").orElseThrow();

        double totalAntes = relatorioService.vendasPorMes().stream()
                .mapToDouble(RelatorioService.VendaMes::total).sum();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new ItemPedido(peca, 3)); // 3 x R$ 50,00
        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedidoRepository.save(pedido);

        double totalDepois = relatorioService.vendasPorMes().stream()
                .mapToDouble(RelatorioService.VendaMes::total).sum();
        assertEquals(150.0, totalDepois - totalAntes, 0.001);

        var linha = relatorioService.pecasMaisVendidas(100).stream()
                .filter(l -> l.nome().equals("Peça do relatório"))
                .findFirst().orElseThrow();
        assertEquals(3, linha.unidades());
        assertEquals(150.0, linha.receita(), 0.001);

        // Estoque 2 está abaixo do limite, então a peça aparece em "Estoque baixo"
        assertTrue(relatorioService.estoqueBaixo().stream()
                .anyMatch(p -> p.getNome().equals("Peça do relatório")));
    }
}
