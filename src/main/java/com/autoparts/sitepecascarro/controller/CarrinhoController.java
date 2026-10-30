package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.ItemPedido;
import com.autoparts.sitepecascarro.service.Carrinho;
import com.autoparts.sitepecascarro.service.PedidoService;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {
    private final Carrinho carrinho;
    private final PedidoService pedidoService;
    private final PecaRepository pecaRepository;

    public CarrinhoController(Carrinho carrinho, PedidoService pedidoService, PecaRepository pecaRepository) {
        this.carrinho = carrinho;
        this.pedidoService = pedidoService;
        this.pecaRepository = pecaRepository;
    }

    @GetMapping
    public String ver(Model model) {
        List<ItemPedido> itens = pedidoService.itensDoCarrinho();
        model.addAttribute("itens", itens);
        model.addAttribute("total", pedidoService.total(itens));
        return "carrinho";
    }

    @PostMapping("/adicionar/{id}")
    public String adicionar(@PathVariable Long id, RedirectAttributes redirect) {
        var peca = pecaRepository.findById(id);
        if (peca.isEmpty()) {
            redirect.addFlashAttribute("erro", "Peça não encontrada.");
        } else if (peca.get().getEstoque() == null || peca.get().getEstoque() <= 0) {
            redirect.addFlashAttribute("erro", "Essa peça está sem estoque.");
        } else {
            carrinho.adicionar(id);
            redirect.addFlashAttribute("sucesso", "Peça adicionada ao carrinho.");
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/alterar/{id}")
    public String alterar(@PathVariable Long id, @RequestParam int quantidade) {
        carrinho.alterar(id, quantidade);
        return "redirect:/carrinho";
    }

    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id) {
        carrinho.remover(id);
        return "redirect:/carrinho";
    }

    @PostMapping("/finalizar")
    public String finalizar(Principal principal, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.finalizar(principal.getName());
            redirect.addFlashAttribute("sucesso", "Pedido " + pedido.getNumero() + " confirmado!");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/carrinho";
    }
}
