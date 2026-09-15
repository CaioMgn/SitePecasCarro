package com.autoparts.sitepecascarro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Página inicial e arquivos públicos
                .requestMatchers(
                    "/",
                    "/pecas",
                    "/css/**",
                    "/js/**",
                    "/imagens/**"
                ).permitAll()

                // Área administrativa somente para ADMIN
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                // Cadastro, edição e exclusão de peças: só ADMIN (RN-02)
                .requestMatchers("/pecas/nova", "/pecas/salvar", "/pecas/editar/**", "/pecas/excluir/**")
                .hasRole("ADMIN")

                // Qualquer outra rota exige login
                .anyRequest()
                .authenticated()
            )

            // Login padrão do Spring Security
            .formLogin(form -> form
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        // Usuário comum: pode visualizar as peças
        UserDetails usuarioComum = User.builder()
            .username("usuario")
            .password(encoder.encode("123456"))
            .roles("USER")
            .build();

        // Administrador: acesso à área administrativa
        UserDetails administrador = User.builder()
            .username("admin")
            .password(encoder.encode("admin123"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(
            usuarioComum,
            administrador
        );
    }

    // Codificador das senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}