package main.java.model;

public abstract class Usuario {
    // ENCAPSULAMENTO: Atributos privados
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;
    private String endereco;

    public Usuario() {}

    public Usuario(String nome, String email, String senhaHash, String telefone, String endereco) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Métodos Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    // MÉTODO ABSTRATO -> OBRIGA AS SUBCLASSES A IMPLEMENTAREM
    public abstract String getIdentificador();
}