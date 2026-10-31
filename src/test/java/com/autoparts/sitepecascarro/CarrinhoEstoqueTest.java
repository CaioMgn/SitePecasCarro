package com.autoparts.sitepecascarro;

import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CarrinhoEstoqueTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void naoDeveFinalizarPedidoQuandoQuantidadeExcedeEstoque() throws Exception {

        // Cria uma peça com apenas uma unidade em estoque.
        Peca peca = new Peca(
                "Peça para teste de estoque",
                "Motor",
                100.0,
                "Peça criada para testar estoque insuficiente."
        );

        peca.setMarca("Teste");
        peca.setCompatibilidade("Carro de teste");
        peca.setEstoque(1);
        peca.setImagem("/imagens/pecas/teste.png");

        peca = pecaRepository.saveAndFlush(peca);

        Long pecaId = peca.getId();

        // Guarda a quantidade de pedidos antes do teste.
        long pedidosAntes = pedidoRepository.count();

        // Cria uma sessão que será reutilizada nas requisições.
        MockHttpSession sessao = new MockHttpSession();

        var usuario = SecurityMockMvcRequestPostProcessors.user(
                "cliente@autoparts.com"
        ).roles("USER");

        // Adiciona a mesma peça pela primeira vez.
        mockMvc.perform(post("/carrinho/adicionar/" + pecaId)
                .session(sessao)
                .with(usuario)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));

        // Adiciona a mesma peça pela segunda vez.
        mockMvc.perform(post("/carrinho/adicionar/" + pecaId)
                .session(sessao)
                .with(usuario)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));

        // Tenta finalizar o pedido com duas unidades, mas só existe uma.
        mockMvc.perform(post("/carrinho/finalizar")
                .session(sessao)
                .with(usuario)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"))
                .andExpect(flash().attribute(
                        "erro",
                        containsString("Estoque insuficiente")
                ));

        // Confirma que o estoque continua sendo uma unidade.
        Peca pecaAtualizada = pecaRepository.findById(pecaId).orElseThrow();
        assertEquals(1, pecaAtualizada.getEstoque());

        // Confirma que nenhum pedido novo foi criado.
        assertEquals(pedidosAntes, pedidoRepository.count());
    }
}