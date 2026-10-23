package com.autoparts.sitepecascarro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 1. Desabilita proteção CSRF apenas para o H2 Console conseguir processar
                // requisições
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))

                // 2. Permite que o H2 Console carregue suas telas dentro de <frame> no
                // navegador
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth

                        // Página inicial, arquivos estáticos, Rota de Cadastro e H2 Console liberados
                        .requestMatchers(
                                "/",
                                "/pecas",
                                "/cadastrar",
                                "/usuarios/salvar",
                                "/css/**",
                                "/js/**",
                                "/imagens/**")
                        .permitAll()

                        // Área administrativa somente para ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Gerenciamento de peças: só ADMIN
                        .requestMatchers("/pecas/nova", "/pecas/salvar", "/pecas/editar/**", "/pecas/excluir/**")
                        .hasRole("ADMIN")

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated())

                // Login padrão do Spring Security
                .formLogin(form -> form.permitAll())

                // Logout
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    // Codificador das senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}