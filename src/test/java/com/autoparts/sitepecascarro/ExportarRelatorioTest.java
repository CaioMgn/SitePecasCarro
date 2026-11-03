package com.autoparts.sitepecascarro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExportarRelatorioTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminBaixaORelatorioEmCsv() throws Exception {
        mockMvc.perform(get("/admin/relatorios/exportar").with(user("admin@autoparts.com").roles("ADMIN")))
               .andExpect(status().isOk())
               .andExpect(header().string("Content-Disposition", containsString("attachment")))
               .andExpect(header().string("Content-Type", containsString("text/csv")))
               .andExpect(content().string(containsString("Vendas por mês")))
               .andExpect(content().string(containsString("Estoque baixo")));
    }

    @Test
    void clienteNaoBaixaORelatorio() throws Exception {
        mockMvc.perform(get("/admin/relatorios/exportar").with(user("cliente@autoparts.com").roles("USER")))
               .andExpect(status().isForbidden());
    }
}
