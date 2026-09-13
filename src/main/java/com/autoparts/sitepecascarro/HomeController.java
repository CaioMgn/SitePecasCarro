package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.service.PecaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PecaService pecaService;

    public HomeController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("tituloPagina", "AutoParts - Peças Automotivas");
        model.addAttribute("tituloPrincipal", "ENCONTRE A PEÇA");
        model.addAttribute("pecas", pecaService.listarTodas());

        return "index";
    }
}