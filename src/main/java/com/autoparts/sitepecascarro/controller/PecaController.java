package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.service.PecaService;
import com.autoparts.sitepecascarro.entity.Peca;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; //RECEBE OS DADOS
import org.springframework.web.bind.annotation.ModelAttribute; // PEGAR O FORMULÁRIO PREENCHIDO
import org.springframework.web.bind.annotation.PathVariable; // PEGAR O ID DA URL
import org.springframework.web.bind.annotation.RequestParam; // PEGAR OS FILTROS DA URL



//Ele recebe a requisição do navegador, 
//chama o Service pra buscar o que precisa,
//e devolve o nome da página HTML que deve aparecer.
@Controller
public class PecaController {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    // catálogo: os filtros chegam pela URL, ex.: /pecas?busca=freio&categoria=Freios
    @GetMapping("/pecas")
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false) String categoria,
                         @RequestParam(required = false) String marca,
                         @RequestParam(required = false) Double precoMax,
                         Model model) {
        model.addAttribute("pecas", pecaService.buscar(busca, categoria, marca, precoMax));
        model.addAttribute("categorias", pecaService.listarCategorias());
        model.addAttribute("marcas", pecaService.listarMarcas());
        // devolve o que a pessoa escolheu, para os filtros continuarem marcados
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("marcaSelecionada", marca);
        model.addAttribute("precoMax", precoMax);
        return "pecas/lista";
    }

    // página de detalhe, aberta para visitantes
    @GetMapping("/pecas/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Peca peca = pecaService.buscarPorId(id);
        if (peca == null) {
            return "redirect:/pecas";
        }
        model.addAttribute("peca", peca);
        return "pecas/detalhe";
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
