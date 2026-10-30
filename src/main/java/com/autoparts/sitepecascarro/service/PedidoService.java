package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.entity.*;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.repository.PedidoRepository;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {
    private final Carrinho carrinho;
    private final PecaRepository pecaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(Carrinho carrinho, PecaRepository pecaRepository,
                         PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.carrinho = carrinho;
        this.pecaRepository = pecaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ItemPedido> itensDoCarrinho() {
        List<ItemPedido> itens = new ArrayList<>();
        for (Map.Entry<Long, Integer> entrada : carrinho.getItens().entrySet()) {
            pecaRepository.findById(entrada.getKey()).ifPresent(peca ->
                itens.add(new ItemPedido(peca, entrada.getValue())));
        }
        return itens;
    }

    public double total(List<ItemPedido> itens) {
        return itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    @Transactional
    public Pedido finalizar(String emailCliente) {
        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("Seu carrinho está vazio.");
        }
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
            .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o cliente logado."));
        Pedido pedido = new Pedido(cliente);

        for (Map.Entry<Long, Integer> entrada : carrinho.getItens().entrySet()) {
            Peca peca = pecaRepository.findById(entrada.getKey())
                .orElseThrow(() -> new IllegalArgumentException("Uma peça do carrinho não está mais disponível."));
            int quantidade = entrada.getValue();
            if (quantidade <= 0) throw new IllegalArgumentException("A quantidade do item é inválida.");
            if (peca.getEstoque() == null || peca.getEstoque() < quantidade) {
                throw new IllegalArgumentException("Estoque insuficiente para " + peca.getNome() + ".");
            }
            if (peca.getPreco() == null || peca.getPreco() < 0) {
                throw new IllegalArgumentException("O preço de " + peca.getNome() + " é inválido.");
            }
            peca.setEstoque(peca.getEstoque() - quantidade);
            pedido.adicionarItem(new ItemPedido(peca, quantidade));
        }

        Pedido salvo = pedidoRepository.save(pedido);
        carrinho.limpar();
        return salvo;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAllByOrderByDataDesc();
    }

    @Transactional
    public void alterarStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if (status == null) throw new IllegalArgumentException("Selecione um status válido.");
        pedido.setStatus(status);
    }
}
