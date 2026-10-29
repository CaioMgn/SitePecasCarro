package com.autoparts.sitepecascarro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//representa uma tabela do banco.
//Cada objeto dessa classe vira uma linha na tabela; cada atributo vira uma coluna.
@Entity
public class Peca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private Double preco;
    private String descricao;
    private String marca;
    private String compatibilidade;
    private Integer estoque;
    private String imagem; // caminho da foto, ex.: /imagens/pecas/alternador.png

    public Peca() {
    }

    public Peca(String nome, String categoria, Double preco, String descricao) {
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public Double getPreco() {
        return preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCompatibilidade() {
        return compatibilidade;
    }

    public void setCompatibilidade(String compatibilidade) {
        this.compatibilidade = compatibilidade;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    
}