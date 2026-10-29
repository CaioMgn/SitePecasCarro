package com.autoparts.sitepecascarro.dto;

public class ClienteResumo {

    private final String nome;
    private final String email;
    private final int clienteDesde;
    private final long pedidos;

    public ClienteResumo(String nome, String email, int clienteDesde, long pedidos) {
        this.nome = nome;
        this.email = email;
        this.clienteDesde = clienteDesde;
        this.pedidos = pedidos;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public int getClienteDesde() { return clienteDesde; }
    public long getPedidos() { return pedidos; }
}