package com.autoparts.sitepecascarro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.repository.PecaRepository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImagemOpcionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PecaRepository pecaRepository;

    @Test
    void adminSalvaPecaSemImagem() throws Exception {
        mockMvc.perform(post("/pecas/salvar")
                        .param("nome", "Peça sem foto teste")
                        .param("categoria", "Freios")
                        .param("preco", "99.90")
                        .param("descricao", "Cadastrada sem imagem")
                        .param("marca", "MarcaTesteSemFoto")
                        .param("compatibilidade", "Gol 1.0")
                        .param("estoque", "3")
                        .param("imagem", "")
                        .with(user("admin@autoparts.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Peca salva = pecaRepository.findAll().stream()
                .filter(p -> "Peça sem foto teste".equals(p.getNome()))
                .findFirst().orElseThrow();
        assertTrue(salva.getImagem() == null || salva.getImagem().isBlank());
    }

    @Test
    void formularioMostraImagemComoOpcional() throws Exception {
        mockMvc.perform(get("/pecas/nova").with(user("admin@autoparts.com").roles("ADMIN")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Caminho da imagem (opcional)")));
    }
}
