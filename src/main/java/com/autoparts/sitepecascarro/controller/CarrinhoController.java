package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.ItemPedido;
import com.autoparts.sitepecascarro.service.Carrinho;
import com.autoparts.sitepecascarro.service.PedidoService;
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

    public CarrinhoController(Carrinho carrinho, PedidoService pedidoService) {
        this.carrinho = carrinho;
        this.pedidoService = pedidoService;
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
        try {
            pedidoService.adicionarAoCarrinho(id);
            redirect.addFlashAttribute("sucesso", "Peça adicionada ao carrinho.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
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
