package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.dto.ClienteResumo;
import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final UsuarioRepository usuarioRepository;

    public ClienteService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<ClienteResumo> listarClientes() {
        return usuarioRepository.findByPapelOrderByNomeAsc(Papel.USER).stream()
                .map(this::resumir)
                .toList();
    }

    private ClienteResumo resumir(Usuario usuario) {
        int ano = usuario.getDataCadastro() != null
                ? usuario.getDataCadastro().getYear()
                : 0;

        // ENCAIXE 1: quando o Caio subir o Pedido, trocar o 0 por
        // pedidoRepository.countByCliente(usuario)
        long pedidos = 0;

        return new ClienteResumo(usuario.getNome(), usuario.getEmail(), ano, pedidos);
    }
}