package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.dto.ClienteResumo;
import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.autoparts.sitepecascarro.repository.PedidoRepository;

import java.util.List;

@Service
public class ClienteService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
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

        long pedidos = pedidoRepository.countByCliente(usuario);

        return new ClienteResumo(usuario.getNome(), usuario.getEmail(), ano, pedidos);
    }
}