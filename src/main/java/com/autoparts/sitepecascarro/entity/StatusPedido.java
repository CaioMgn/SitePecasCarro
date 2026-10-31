
package com.autoparts.sitepecascarro.entity;

public enum StatusPedido {

    PENDENTE("Pendente"),
    EM_PROCESSAMENTO("Em processamento"),
    ENVIADO("Enviado"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
