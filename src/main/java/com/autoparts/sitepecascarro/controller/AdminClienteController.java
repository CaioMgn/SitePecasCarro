package com.autoparts.sitepecascarro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.autoparts.sitepecascarro.service.ClienteService;
import com.autoparts.sitepecascarro.service.PainelService;

@Controller
public class AdminClienteController {

    private final ClienteService clienteService;
    private final PainelService painelService;

    public AdminClienteController(
            ClienteService clienteService,
            PainelService painelService) {
        this.clienteService = clienteService;
        this.painelService = painelService;
    }

    @GetMapping("/admin")
    public String painel(Model model) {
        model.addAttribute("totalPecas", painelService.totalPecas());
        model.addAttribute("pedidosPendentes", painelService.pedidosPendentes());
        model.addAttribute("clientesAtivos", painelService.clientesAtivos());
        model.addAttribute("faturamento", painelService.faturamento());

        return "admin/index";
    }

    @GetMapping("/admin/clientes")
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "admin/clientes";
    }
}