package com.autoparts.sitepecascarro.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class Carrinho {
    private final Map<Long, Integer> itens = new LinkedHashMap<>();

    public void adicionar(Long pecaId) {
        itens.merge(pecaId, 1, Integer::sum);
    }

    public void alterar(Long pecaId, int quantidade) {
        if (quantidade <= 0) itens.remove(pecaId);
        else itens.put(pecaId, quantidade);
    }

    public void remover(Long pecaId) { itens.remove(pecaId); }
    public Map<Long, Integer> getItens() { return itens; }
    public void limpar() { itens.clear(); }
}
