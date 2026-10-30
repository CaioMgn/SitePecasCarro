package com.autoparts.sitepecascarro.entity;

import jakarta.persistence.*;

@Entity
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    private Peca peca;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double precoUnitario;

    public ItemPedido() { }

    public ItemPedido(Peca peca, Integer quantidade) {
        this.peca = peca;
        this.quantidade = quantidade;
        this.precoUnitario = peca.getPreco();
    }

    public Double getSubtotal() {
        return (precoUnitario == null ? 0.0 : precoUnitario) * (quantidade == null ? 0 : quantidade);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
}
