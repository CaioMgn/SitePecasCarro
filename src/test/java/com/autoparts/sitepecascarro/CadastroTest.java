package com.autoparts.sitepecascarro;

import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CadastroTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    void cadastroComPapelAdminEscondidoCriaUsuarioComum() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .param("nome", "Atacante")
                        .param("email", "atacante@teste.com")
                        .param("senha", "123456")
                        .param("papel", "ADMIN")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastrado"));

        Usuario criado = usuarioRepository.findByEmail("atacante@teste.com").orElseThrow();
        assertEquals(Papel.USER, criado.getPapel());
    }

    @Test
    void emailRepetidoVoltaParaOFormularioComErro() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .param("nome", "Outro Admin")
                        .param("email", "admin@autoparts.com")
                        .param("senha", "123456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attribute("erro", "Este e-mail já está cadastrado."));
    }
}