package com.autoparts.sitepecascarro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.service.UsuarioService;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Injeção de dependência via construtor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * 1. ROTA GET: Exibe o formulário de cadastro público de usuários.
     * Mapeado na rota "/cadastrar" (que liberamos no SecurityConfig).
     */
    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        // Envia um objeto Usuario vazio para o Thymeleaf vincular aos campos do
        // formulário
        model.addAttribute("usuario", new Usuario());
        return "usuarios/cadastrar_usuarios"; // Carrega o arquivo cadastrar.html em templates
    }

    /**
     * 2. ROTA POST: Processa os dados enviados pelo formulário.
     * Corresponde ao th:action="@{/usuarios/salvar}" do seu HTML.
     */
    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute("usuario") Usuario usuario,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Executa as validações de CPF, E-mail, Duplicidade e Criptografia da Senha
            usuarioService.salvar(usuario);

            // Mensagem de sucesso enviada após o redirecionamento
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça seu login.");
            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException e) {
            // Se a Service lançar exceção (ex: "CPF inválido."), captura a mensagem
            model.addAttribute("erro", e.getMessage());

            // Retorna para a mesma tela cadastrar.html, mantendo os dados preenchidos no
            // form
            return "usuarios/cadastrar_usuarios";
        }
    }

    /**
     * 3. ROTA GET: Listagem de Usuários (Restrita ao ADMIN)
     */
    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model) {
        // Envia a lista de usuários para a tela de listagem
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/usuarios-listar"; // Nome do seu arquivo HTML de listagem
    }

    /**
     * 4. ROTA GET/POST: Excluir Usuário pelo CPF (Restrito ao ADMIN)
     */
    @GetMapping("/admin/usuarios/excluir/{cpf}")
    public String excluirUsuario(@PathVariable("cpf") String cpf, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(cpf);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir usuário.");
        }
        return "redirect:/usuarios/usuarios-listar";
    }
}