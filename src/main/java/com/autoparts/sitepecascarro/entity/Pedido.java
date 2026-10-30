package com.autoparts.sitepecascarro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario cliente;

    private LocalDateTime data;
    private Double total = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() { }

    public Pedido(Usuario cliente) {
        this.cliente = cliente;
        this.data = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
        this.total = 0.0;
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
        total += item.getSubtotal();
    }

    public String getNumero() {
        return id == null ? "PED-novo" : "PED-" + (1000 + id);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public Double getTotal() { return total == null ? 0.0 : total; }
    public void setTotal(Double total) { this.total = total; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}
