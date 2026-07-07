package model;

import exception.PrecoInvalidoException;

public class ItemCardapio {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private CategoriaItem categoria;
    private boolean disponivel;

    // Construtor vazio
    public ItemCardapio() {  // ← CORRIGIDO: sem parâmetros
        this.id = 0;
        this.nome = "";
        this.descricao = "";
        this.preco = 0.0;
        this.categoria = null;
        this.disponivel = true;
    }

    // Construtor completo
    public ItemCardapio(int id, String nome, String descricao, double preco, CategoriaItem categoria)
            throws PrecoInvalidoException {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        setPreco(preco);
        this.categoria = categoria;
        this.disponivel = true;
    }

    // Construtor simplificado (sem exceção)
    public ItemCardapio(int id, String nome, double preco, CategoriaItem categoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = "";
        this.preco = preco;
        this.categoria = categoria;
        this.disponivel = true;
    }

    // Setters com validação
    public void setPreco(double preco) throws PrecoInvalidoException {
        if (preco <= 0) {
            throw new PrecoInvalidoException("Preço deve ser maior que zero.");
        }
        this.preco = preco;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public CategoriaItem getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaItem categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    @Override
    public String toString() {
        return String.format("%s (R$ %.2f) - %s",
                nome, preco, disponivel ? "Disponível" : "Indisponível");
    }
}