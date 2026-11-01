package com.autoparts.sitepecascarro.controller;

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
}
