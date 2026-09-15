package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.service.PecaService;
import com.autoparts.sitepecascarro.entity.Peca;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; //RECEBE OS DADOS
import org.springframework.web.bind.annotation.ModelAttribute; // PEGAR O FORMULÁRIO PREENCHIDO
import org.springframework.web.bind.annotation.PathVariable; // PEGAR O ID DA URL



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

    // mostrar o formulário vazio
    @GetMapping("/pecas/nova")
    public String novaPeca(Model model) {
        model.addAttribute("peca", new Peca());
        return "pecas/formulario";
    }
    
    //salvar o formulário
    @PostMapping("/pecas/salvar")
    public String salvar(@ModelAttribute Peca peca) {
        pecaService.salvar(peca);
        return "redirect:/pecas";
    }
    
    //mostrar o formulário preenchido
    @GetMapping("/pecas/editar/{id}")
    public String editarPeca(@PathVariable Long id, Model model) {
        model.addAttribute("peca", pecaService.buscarPorId(id));
        return "pecas/formulario";
    }
    
    @GetMapping("/pecas/excluir/{id}")
    public String excluirPeca(@PathVariable Long id) {
        pecaService.excluir(id);
        return "redirect:/pecas";
    }
    

}
