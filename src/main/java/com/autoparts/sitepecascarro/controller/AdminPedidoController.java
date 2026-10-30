package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.entity.StatusPedido;
import com.autoparts.sitepecascarro.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPedidoController {
    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/admin/pedidos")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("statusDisponiveis", StatusPedido.values());
        return "admin/pedidos";
    }

    @PostMapping("/admin/pedidos/{id}/status")
    public String alterarStatus(@PathVariable Long id, @RequestParam StatusPedido status,
                                RedirectAttributes redirect) {
        try {
            pedidoService.alterarStatus(id, status);
            redirect.addFlashAttribute("sucesso", "Status do pedido atualizado.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }
}
