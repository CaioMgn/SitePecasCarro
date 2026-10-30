package com.autoparts.sitepecascarro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LayoutTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void homeTemEstruturaSemantica() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("id=\"conteudo\"")))
               .andExpect(content().string(containsString("<footer")))
               .andExpect(content().string(containsString("aria-label=\"Principal\"")));
    }

    @Test
    void visitanteVeEntrarENaoVeSair() throws Exception {
        mockMvc.perform(get("/pecas"))
               .andExpect(content().string(containsString(">Entrar<")))
               .andExpect(content().string(not(containsString(">Sair<"))));
    }

    @Test
    void clienteLogadoVeSair() throws Exception {
        mockMvc.perform(get("/pecas").with(user("cliente@autoparts.com").roles("USER")))
               .andExpect(content().string(containsString(">Sair<")));
    }
}