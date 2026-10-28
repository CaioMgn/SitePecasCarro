package com.autoparts.sitepecascarro.config;

import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@autoparts.com")) {
            usuarioRepository.save(new Usuario(
                    "Administrador",
                    "admin@autoparts.com",
                    passwordEncoder.encode("admin123"),
                    Papel.ADMIN));
        }

        if (!usuarioRepository.existsByEmail("cliente@autoparts.com")) {
            usuarioRepository.save(new Usuario(
                    "Cliente Teste",
                    "cliente@autoparts.com",
                    passwordEncoder.encode("123456"),
                    Papel.USER));
        }
    }
}
