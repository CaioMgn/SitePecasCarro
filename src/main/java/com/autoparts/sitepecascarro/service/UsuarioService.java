package com.autoparts.sitepecascarro.service;

import org.springframework.stereotype.Service;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;


@Service 
public class UsuarioService {
   private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioService usuarioRepository){
        this.usuarioRepository = (UsuarioRepository) usuarioRepository;
    }

    public Usuario salvar(Usuario usuario){
        if (!cpfValido(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        if (!emailValido(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (usuarioRepository.existsById(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        return usuarioRepository.save(usuario);
    }

    public void excluir(String cpf){
        usuarioRepository.deleteById(cpf);
    }

    private boolean cpfValido(String cpf){
        if(cpf == null){
            return false;
        }

        cpf = cpf.replace("\\d", "");
        
        if(cpf.length() != 11){
            return false;
        }

        if(cpf.chars().distinct().count() == 1){
            return false;
        }

        int soma = 0;

        for(int i = 0; i<9; i++){
            soma += Character.getNumericValue(cpf.charAt(i) * (10 - i));
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
