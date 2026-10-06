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
        return usuarioRepository.save(usuario);
    }

    public void excluir(String cpf){
        usuarioRepository.deleteById(cpf);
    }
}
