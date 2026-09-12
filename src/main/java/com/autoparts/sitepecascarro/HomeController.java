package com.autoparts.sitepecascarro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("tituloPagina", "AutoParts - Peças Automotivas");
        model.addAttribute("tituloPrincipal", "ENCONTRE A PEÇA");

        return "index";
    }
}