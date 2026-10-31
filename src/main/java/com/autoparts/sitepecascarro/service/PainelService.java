package com.autoparts.sitepecascarro.service;

import org.springframework.stereotype.Service;

import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.StatusPedido;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.repository.PedidoRepository;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;

@Service
public class PainelService {

    private final PecaRepository pecaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PainelService(
            PecaRepository pecaRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {

        this.pecaRepository = pecaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public long totalPecas() {
        return pecaRepository.count();
    }

    public long pedidosPendentes() {
        return pedidoRepository.countByStatus(StatusPedido.PENDENTE);
    }

    public long clientesAtivos() {
        return usuarioRepository.countByPapel(Papel.USER);
    }

    public double faturamento() {
    return pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getStatus() == StatusPedido.CONCLUIDO)
            .mapToDouble(pedido -> pedido.getTotal())
            .sum();
}
}