package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.dto.CadastroForm;
import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrarCliente(CadastroForm form) {
        String email = form.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        if (form.getSenha() == null || form.getSenha().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        Usuario usuario = new Usuario(
                form.getNome().trim(),
                email,
                passwordEncoder.encode(form.getSenha()),
                Papel.USER);

        return usuarioRepository.save(usuario);
    }
}