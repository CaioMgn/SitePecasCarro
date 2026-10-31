package com.autoparts.sitepecascarro;

import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.service.PecaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogoTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PecaService pecaService;

    @Autowired
    PecaRepository pecaRepository;

    private Peca criarPeca(String nome, String categoria, Double preco, String marca, Integer estoque) {
    	Peca peca = new Peca(nome, categoria, preco, "Descrição de teste");

    	peca.setMarca(marca == null || marca.isBlank() ? "MarcaTeste" : marca);
    	peca.setCompatibilidade("Gol 1.0 2010-2015");
    	peca.setEstoque(estoque == null ? 0 : estoque);
    	peca.setImagem("/imagens/pecas/teste.png");

    	return pecaRepository.save(peca);
	}

    // ---------- Service: busca e filtros ----------

    @Test
    void buscarPorCategoriaFreiosDevolveSoPecasDeFreio() {
        Peca disco = criarPeca("Disco de freio teste", "Freios", 250.0, "MarcaTesteA", 5);
        Peca filtro = criarPeca("Filtro de óleo teste", "Motor", 40.0, "MarcaTesteA", 5);

        List<Peca> resultado = pecaService.buscar(null, "Freios", null, null);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().allMatch(p -> "Freios".equals(p.getCategoria())));
        assertTrue(resultado.contains(disco));
        assertFalse(resultado.contains(filtro));
    }

    @Test
    void buscaPorTextoIgnoraMaiusculasEEspacos() {
        Peca disco = criarPeca("Disco de Freio teste", "Freios", 250.0, "MarcaTesteA", 5);
        Peca filtro = criarPeca("Filtro de óleo teste", "Motor", 40.0, "MarcaTesteA", 5);

        List<Peca> resultado = pecaService.buscar("  FREIO ", null, null, null);

        assertTrue(resultado.contains(disco));
        assertFalse(resultado.contains(filtro));
    }

    @Test
    void filtrosCombinadosDeMarcaEPrecoMaximoIncluemOLimite() {
        Peca noLimite = criarPeca("Pastilha teste", "Freios", 100.0, "MarcaTesteB", 5);
        Peca caro = criarPeca("Disco teste", "Freios", 100.01, "MarcaTesteB", 5);
        Peca outraMarca = criarPeca("Pastilha barata teste", "Freios", 50.0, "MarcaTesteC", 5);

        List<Peca> resultado = pecaService.buscar(null, "Freios", "MarcaTesteB", 100.0);

        assertEquals(List.of(noLimite), resultado);
        assertFalse(resultado.contains(caro));
        assertFalse(resultado.contains(outraMarca));
    }

    @Test
    void filtrosVaziosDevolvemTodasAsPecas() {
        criarPeca("Peça qualquer teste", "Motor", 10.0, null, null);

        List<Peca> resultado = pecaService.buscar("", " ", null, null);

        assertEquals(pecaRepository.count(), resultado.size());
    }

    @Test
    void buscaComFiltrosVaziosNaoLancaExcecao() {
    	Peca valida = criarPeca(
            	"Vela de ignição teste",
            	"Motor",
            	100.0,
            	"MarcaTeste",
            	1
    	);

    	List<Peca> porTexto = assertDoesNotThrow(
            	() -> pecaService.buscar("vela", null, null, null)
    	);

    	List<Peca> porPreco = assertDoesNotThrow(
            	() -> pecaService.buscar(null, null, null, 100.0)
    	);

    	assertTrue(porTexto.contains(valida));
    	assertTrue(porPreco.contains(valida));
    }

    @Test
    void listarMarcasSemRepetidasNemVaziasEmOrdem() {
        criarPeca("Peça 1 teste", "Motor", 10.0, "ZZ Marca Teste", 1);
        criarPeca("Peça 2 teste", "Motor", 10.0, "AA Marca Teste", 1);
        criarPeca("Peça 3 teste", "Motor", 10.0, "ZZ Marca Teste", 1);
        criarPeca("Peça 4 teste", "Motor", 10.0, "  ", 1);
        criarPeca("Peça 5 teste", "Motor", 10.0, null, 1);

        List<String> marcas = pecaService.listarMarcas();

        assertEquals(1, marcas.stream().filter("ZZ Marca Teste"::equals).count());
        assertTrue(marcas.indexOf("AA Marca Teste") < marcas.indexOf("ZZ Marca Teste"));
        assertTrue(marcas.stream().noneMatch(String::isBlank));
        assertEquals(marcas.stream().sorted().toList(), marcas);
    }

    @Test
    void listarCategoriasSemRepetidasEmOrdem() {
        criarPeca("Peça 1 teste", "Suspensão", 10.0, null, 1);
        criarPeca("Peça 2 teste", "Suspensão", 10.0, null, 1);
        criarPeca("Peça 3 teste", "Elétrica", 10.0, null, 1);

        List<String> categorias = pecaService.listarCategorias();

        assertEquals(1, categorias.stream().filter("Suspensão"::equals).count());
        assertTrue(categorias.contains("Elétrica"));
        assertEquals(categorias.stream().sorted().toList(), categorias);
    }

    // ---------- Tela do catálogo ----------

    @Test
    void visitanteVeOCatalogoComOsFiltrosMarcados() throws Exception {
        criarPeca("Disco de freio teste", "Freios", 250.0, "MarcaTesteA", 5);

        mockMvc.perform(get("/pecas")
                        .param("busca", "disco")
                        .param("categoria", "Freios")
                        .param("marca", "MarcaTesteA")
                        .param("precoMax", "300"))
                .andExpect(status().isOk())
                .andExpect(view().name("pecas/lista"))
                .andExpect(model().attribute("busca", "disco"))
                .andExpect(model().attribute("categoriaSelecionada", "Freios"))
                .andExpect(model().attribute("marcaSelecionada", "MarcaTesteA"))
                .andExpect(model().attribute("precoMax", 300.0))
                .andExpect(content().string(containsString("value=\"disco\"")))
                .andExpect(content().string(containsString("value=\"Freios\" selected=\"selected\"")))
                .andExpect(content().string(containsString("value=\"MarcaTesteA\" selected=\"selected\"")))
                .andExpect(content().string(containsString("Disco de freio teste")));
    }

    @Test
    void buscaSemResultadoMostraMensagem() throws Exception {
        mockMvc.perform(get("/pecas").param("busca", "nao-existe-peca-com-esse-nome-xyz"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhuma peça encontrada")));
    }

    @Test
    void cardLevaAoDetalheComPrecoNoFormatoBrasileiro() throws Exception {
        Peca peca = criarPeca("Alternador teste", "Elétrica", 1299.5, "MarcaTesteA", 2);

        mockMvc.perform(get("/pecas").param("busca", "Alternador teste"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/pecas/detalhe/" + peca.getId() + "\"")))
                .andExpect(content().string(containsString("1.299,50")));
    }

    @Test
    void catalogoNaoMostraMaisEditarEExcluir() throws Exception {
        criarPeca("Peça do admin teste", "Motor", 10.0, null, 1);

        mockMvc.perform(get("/pecas").with(user("admin@autoparts.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/pecas/editar/"))))
                .andExpect(content().string(not(containsString("/pecas/excluir/"))));
    }

    @Test
    void precoMaximoInvalidoNaURLDevolveErro400() throws Exception {
        mockMvc.perform(get("/pecas").param("precoMax", "abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Página de detalhe ----------

    @Test
    void visitanteAbreODetalheSemLogin() throws Exception {
        Peca peca = criarPeca("Amortecedor teste", "Suspensão", 899.0, "MarcaTesteA", 3);
        peca.setImagem("/imagens/pecas/amortecedor.png");
        pecaRepository.save(peca);

        mockMvc.perform(get("/pecas/detalhe/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("pecas/detalhe"))
                .andExpect(content().string(containsString("Amortecedor teste")))
                .andExpect(content().string(containsString("899,00")))
                .andExpect(content().string(containsString("MarcaTesteA")))
                .andExpect(content().string(containsString("Gol 1.0 2010-2015")))
                .andExpect(content().string(containsString("3 em estoque")))
                .andExpect(content().string(containsString(
                        "src=\"/imagens/pecas/amortecedor.png\" alt=\"Amortecedor teste\"")))
                .andExpect(content().string(containsString("action=\"/carrinho/adicionar/" + peca.getId() + "\"")));
    }

    @Test
    void detalheDePecaInexistenteVoltaAoCatalogo() throws Exception {
        mockMvc.perform(get("/pecas/detalhe/{id}", 999999999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pecas"));
    }

    @Test
    void pecaComEstoqueZeroMostraBotaoDesativado() throws Exception {
        Peca peca = criarPeca("Vela sem estoque teste", "Motor", 30.0, null, 0);

        mockMvc.perform(get("/pecas/detalhe/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sem estoque")))
                .andExpect(content().string(containsString("disabled=\"disabled\"")));
    }

    @Test
    void pecaComEstoqueZeroDesativaOBotao() throws Exception {
    	Peca peca = criarPeca(
            	"Vela antiga teste",
            	"Motor",
            	30.0,
            	null,
            	0
    	);

    	mockMvc.perform(get("/pecas/detalhe/{id}", peca.getId()))
            	.andExpect(status().isOk())
            	.andExpect(content().string(containsString("Sem estoque")))
            	.andExpect(content().string(containsString("disabled=\"disabled\"")));
    }

    @Test
    void pecaComEstoqueMostraBotaoAtivo() throws Exception {
        Peca peca = criarPeca("Vela com estoque teste", "Motor", 30.0, null, 4);

        mockMvc.perform(get("/pecas/detalhe/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("disabled"))));
    }

    // ---------- Segurança: o permitAll do detalhe não abriu demais ----------

    @Test
    void visitanteContinuaSemAcessoAoCadastroDePecas() throws Exception {
        mockMvc.perform(get("/pecas/nova"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void visitanteQueAdicionaAoCarrinhoVaiParaOLogin() throws Exception {
        Peca peca = criarPeca("Peça para carrinho teste", "Motor", 30.0, null, 4);

        mockMvc.perform(post("/carrinho/adicionar/{id}", peca.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // ---------- Formulário com os campos novos ----------

    @Test
    void formularioMostraOsCamposNovos() throws Exception {
        mockMvc.perform(get("/pecas/nova").with(user("admin@autoparts.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("for=\"marca\"")))
                .andExpect(content().string(containsString("for=\"compatibilidade\"")))
                .andExpect(content().string(containsString("id=\"estoque\"")))
                .andExpect(content().string(containsString("for=\"imagem\"")));
    }

    @Test
    void adminSalvaPecaComOsCamposNovos() throws Exception {
        mockMvc.perform(post("/pecas/salvar")
                        .param("nome", "Bomba de combustível teste")
                        .param("categoria", "Motor")
                        .param("preco", "450.90")
                        .param("descricao", "Bomba elétrica")
                        .param("marca", "MarcaTesteD")
                        .param("compatibilidade", "Uno 1.0")
                        .param("estoque", "7")
                        .param("imagem", "/imagens/pecas/bomba.png")
                        .with(user("admin@autoparts.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Peca salva = pecaRepository.findAll().stream()
                .filter(p -> "Bomba de combustível teste".equals(p.getNome()))
                .findFirst().orElseThrow();
        assertEquals("MarcaTesteD", salva.getMarca());
        assertEquals("Uno 1.0", salva.getCompatibilidade());
        assertEquals(7, salva.getEstoque());
        assertEquals("/imagens/pecas/bomba.png", salva.getImagem());
    }
}
