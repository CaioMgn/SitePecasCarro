package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado."));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getSenha())
                .roles(usuario.getRole() == com.autoparts.sitepecascarro.entity.Role.ADMIN
                        ? "ADMIN"
                        : "USER")
                .build();
    }
}