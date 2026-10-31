package com.autoparts.sitepecascarro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.autoparts.sitepecascarro.service.PecaService;

@Controller
public class AdminPecaController {

    private final PecaService pecaService;

    public AdminPecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping("/admin/produtos")
    public String listarProdutos(Model model) {
        model.addAttribute("pecas", pecaService.listarTodas());
        return "admin/produtos";
    }
}
