package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.service.PecaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//Ele recebe a requisição do navegador, 
//chama o Service pra buscar o que precisa,
//e devolve o nome da página HTML que deve aparecer.
@Controller
public class PecaController {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping("/pecas")
    public String listar(Model model) {
        model.addAttribute("pecas", pecaService.listarTodas());
        return "pecas/lista";
    }
}
