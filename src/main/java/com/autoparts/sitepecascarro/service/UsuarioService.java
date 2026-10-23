package com.autoparts.sitepecascarro.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.autoparts.sitepecascarro.entity.Role;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passswordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passswordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {
        // 1\. Tratamento do CPF: remove pontos e traços para trabalhar e salvar apenas
        // com números
        if (usuario.getCpf() != null) {
            usuario.setCpf(usuario.getCpf().replaceAll("\\\\D", ""));
        }
        // 2\. Validação do CPF formatado (apenas 11 dígitos)
        if (!cpfValido(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        // 3\. Validação do formato do E-mail
        if (!emailValido(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        // 4\. Verificação de duplicidade no banco (usando o CPF limpo)
        if (usuarioRepository.existsById(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        // 5\. Garantia de perfil: se vier nulo (cadastro público), atribui perfil de
        // Usuário comum
        if (usuario.getRole() == null) {
            usuario.setRole(Role.USER);
        }
        // 6\. Criptografia da senha antes da persistência
        usuario.setSenha(passswordEncoder.encode(usuario.getSenha()));

        // 7\. Persistência no banco H2
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void excluir(String cpf) {
        usuarioRepository.deleteById(cpf);
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replace("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int resto = soma % 11;
        int primeiroDigito = resto < 2 ? 0 : 11 - resto;

        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        resto = soma % 11;

        int segundoDigito = resto < 2 ? 0 : 11 - resto;

        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    private boolean emailValido(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
