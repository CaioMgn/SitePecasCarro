package com.autoparts.sitepecascarro.controller;

import com.autoparts.sitepecascarro.dto.CadastroForm;
import com.autoparts.sitepecascarro.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("form", new CadastroForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute("form") CadastroForm form, Model model) {
        try {
            usuarioService.cadastrarCliente(form);
            return "redirect:/login?cadastrado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            form.setSenha(null);
            return "cadastro";
        }
    }
}