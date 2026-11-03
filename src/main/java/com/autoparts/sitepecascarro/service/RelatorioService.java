package com.autoparts.sitepecascarro.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoparts.sitepecascarro.entity.ItemPedido;
import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.StatusPedido;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import com.autoparts.sitepecascarro.repository.PedidoRepository;

// Monta as tabelas da tela Relatórios agrupando os dados com streams (sem SQL).
// Conta todos os pedidos feitos; a coluna "Concluído" mostra a parte que já fechou,
// que é o mesmo critério do card de faturamento da Visão geral.
@Service
@Transactional(readOnly = true)
public class RelatorioService {

    // Peças com esta quantidade ou menos aparecem em "Estoque baixo"
    public static final int LIMITE_ESTOQUE_BAIXO = 5;

    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));

    // Linhas das tabelas (records: classes só com dados)
    public record VendaMes(String mes, long pedidos, double total, double concluido) { }
    public record VendaCategoria(String categoria, int unidades, double receita) { }
    public record PecaVendida(String nome, String categoria, int unidades, double receita) { }

    private final PedidoRepository pedidoRepository;
    private final PecaRepository pecaRepository;

    public RelatorioService(PedidoRepository pedidoRepository, PecaRepository pecaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pecaRepository = pecaRepository;
    }

    // Vendas por período: um grupo por mês. TreeMap deixa os meses em ordem.
    public List<VendaMes> vendasPorMes() {
        Map<YearMonth, List<Pedido>> porMes = pedidoRepository.findAll().stream()
                .filter(p -> p.getData() != null)
                .collect(Collectors.groupingBy(p -> YearMonth.from(p.getData()), TreeMap::new, Collectors.toList()));

        List<VendaMes> linhas = new ArrayList<>();
        for (Map.Entry<YearMonth, List<Pedido>> grupo : porMes.entrySet()) {
            List<Pedido> pedidos = grupo.getValue();
            double total = pedidos.stream().mapToDouble(Pedido::getTotal).sum();
            double concluido = pedidos.stream()
                    .filter(p -> p.getStatus() == StatusPedido.CONCLUIDO)
                    .mapToDouble(Pedido::getTotal)
                    .sum();
            String mes = grupo.getKey().format(FORMATO_MES);
            linhas.add(new VendaMes(mes, pedidos.size(), total, concluido));
        }
        return linhas;
    }

    // Vendas por categoria: soma os itens de todos os pedidos
    public List<VendaCategoria> vendasPorCategoria() {
        Map<String, List<ItemPedido>> porCategoria = todosOsItens().stream()
                .filter(i -> i.getPeca().getCategoria() != null)
                .collect(Collectors.groupingBy(i -> i.getPeca().getCategoria(), TreeMap::new, Collectors.toList()));

        List<VendaCategoria> linhas = new ArrayList<>();
        porCategoria.forEach((categoria, itens) ->
                linhas.add(new VendaCategoria(categoria, somaUnidades(itens), somaReceita(itens))));
        return linhas;
    }

    // Peças mais vendidas: ranking por unidades vendidas
    public List<PecaVendida> pecasMaisVendidas(int limite) {
        Map<Peca, List<ItemPedido>> porPeca = todosOsItens().stream()
                .collect(Collectors.groupingBy(ItemPedido::getPeca));

        return porPeca.entrySet().stream()
                .map(e -> new PecaVendida(
                        e.getKey().getNome(),
                        e.getKey().getCategoria(),
                        somaUnidades(e.getValue()),
                        somaReceita(e.getValue())))
                .sorted(Comparator.comparingInt(PecaVendida::unidades).reversed()
                        .thenComparing(PecaVendida::nome))
                .limit(limite)
                .toList();
    }

    // Unidades em estoque por categoria
    public Map<String, Integer> estoquePorCategoria() {
        return pecaRepository.findAll().stream()
                .filter(p -> p.getCategoria() != null)
                .collect(Collectors.groupingBy(Peca::getCategoria, TreeMap::new,
                        Collectors.summingInt(p -> p.getEstoque() == null ? 0 : p.getEstoque())));
    }

    // Peças que estão acabando, da menor quantidade para a maior
    public List<Peca> estoqueBaixo() {
        return pecaRepository.findAll().stream()
                .filter(p -> p.getEstoque() == null || p.getEstoque() <= LIMITE_ESTOQUE_BAIXO)
                .sorted(Comparator.comparing((Peca p) -> p.getEstoque() == null ? 0 : p.getEstoque())
                        .thenComparing(Peca::getNome))
                .toList();
    }

    // ---------- Exportação em CSV ----------
    // Separador ";" e vírgula nos decimais: é o formato que o Excel em português abre direto.
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public String exportarCsv() {
        StringBuilder csv = new StringBuilder();

        csv.append("Vendas por mês\n");
        csv.append("Mês;Pedidos;Total (R$);Concluído (R$)\n");
        for (VendaMes linha : vendasPorMes()) {
            csv.append(campo(linha.mes())).append(';')
               .append(linha.pedidos()).append(';')
               .append(dinheiro(linha.total())).append(';')
               .append(dinheiro(linha.concluido())).append('\n');
        }

        csv.append("\nVendas por categoria\n");
        csv.append("Categoria;Unidades;Receita (R$)\n");
        for (VendaCategoria linha : vendasPorCategoria()) {
            csv.append(campo(linha.categoria())).append(';')
               .append(linha.unidades()).append(';')
               .append(dinheiro(linha.receita())).append('\n');
        }

        csv.append("\nPeças mais vendidas\n");
        csv.append("Peça;Categoria;Unidades;Receita (R$)\n");
        for (PecaVendida linha : pecasMaisVendidas(10)) {
            csv.append(campo(linha.nome())).append(';')
               .append(campo(linha.categoria())).append(';')
               .append(linha.unidades()).append(';')
               .append(dinheiro(linha.receita())).append('\n');
        }

        csv.append("\nEstoque baixo (até ").append(LIMITE_ESTOQUE_BAIXO).append(" unidades)\n");
        csv.append("Peça;Categoria;Estoque\n");
        for (Peca peca : estoqueBaixo()) {
            csv.append(campo(peca.getNome())).append(';')
               .append(campo(peca.getCategoria())).append(';')
               .append(peca.getEstoque() == null ? 0 : peca.getEstoque()).append('\n');
        }

        return csv.toString();
    }

    private String dinheiro(double valor) {
        return String.format(PT_BR, "%.2f", valor);
    }

    // Se o texto tiver ; aspas ou quebra de linha, ele vai entre aspas (regra do CSV)
    private String campo(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private List<ItemPedido> todosOsItens() {
        return pedidoRepository.findAll().stream()
                .flatMap(p -> p.getItens().stream())
                .toList();
    }

    private int somaUnidades(List<ItemPedido> itens) {
        return itens.stream().mapToInt(i -> i.getQuantidade() == null ? 0 : i.getQuantidade()).sum();
    }

    private double somaReceita(List<ItemPedido> itens) {
        return itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }
}