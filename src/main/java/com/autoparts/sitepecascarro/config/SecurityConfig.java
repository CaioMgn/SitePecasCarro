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

                // Console do banco: só ADMIN
                .requestMatchers("/h2-console/**")
                .hasRole("ADMIN")

                // Qualquer outra rota exige login
                .anyRequest()
                .authenticated()
            )

            // Tela de login própria
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", false)
                .permitAll()
            )

            // Logout volta pro login com aviso
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            
            // O console do H2 envia formulários sem token CSRF
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )

            // O console do H2 roda dentro de frames (iframes)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    // Codificador das senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}