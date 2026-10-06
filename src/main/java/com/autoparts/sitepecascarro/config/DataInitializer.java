package com.autoparts.sitepecascarro.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.entity.Role;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration 
public class DataInitializer {
    
    @Bean 
    public  CommandLineRunner inicializarusuario(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ){
        return args -> {
            if(usuarioRepository.count() == 0){
                Usuario admin = new Usuario(
                    "00000000191",
                    "admin@autoparts.com",
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN,
                    "admin"
                );

                usuarioRepository.save(admin);
            }
        };
    }
}
