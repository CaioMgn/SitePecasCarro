package com.autoparts.sitepecascarro.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.autoparts.sitepecascarro.service.RelatorioService;

// Tela Relatórios do painel (o /admin/** já é só para ADMIN no SecurityConfig)
@Controller
public class AdminRelatorioController {

    private final RelatorioService relatorioService;

    public AdminRelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/admin/relatorios")
    public String relatorios(Model model) {
        model.addAttribute("vendasPorMes", relatorioService.vendasPorMes());
        model.addAttribute("vendasPorCategoria", relatorioService.vendasPorCategoria());
        model.addAttribute("maisVendidas", relatorioService.pecasMaisVendidas(10));
        model.addAttribute("estoquePorCategoria", relatorioService.estoquePorCategoria());
        model.addAttribute("estoqueBaixo", relatorioService.estoqueBaixo());
        model.addAttribute("limiteEstoqueBaixo", RelatorioService.LIMITE_ESTOQUE_BAIXO);
        return "admin/relatorios";
    }

    // Baixa o relatório como CSV (abre no Excel). Também fica sob /admin/**, então só ADMIN acessa.
    @GetMapping("/admin/relatorios/exportar")
    public ResponseEntity<byte[]> exportar() {
        // O caractere BOM no começo avisa o Excel que o arquivo é UTF-8 (senão os acentos quebram)
        byte[] arquivo = ("\uFEFF" + relatorioService.exportarCsv()).getBytes(StandardCharsets.UTF_8);
        String nome = "relatorio-autoparts-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nome + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(arquivo);
    }
}