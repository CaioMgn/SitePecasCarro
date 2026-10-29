package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminClienteController {

    private final ClienteService clienteService;

    public AdminClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/admin/clientes")
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "admin/clientes";
    }
}