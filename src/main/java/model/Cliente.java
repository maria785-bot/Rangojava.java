package main.java.model;

public class Cliente extends Usuario {
    private String cpf;

    public Cliente() {}

    public Cliente(String nome, String email, String senhaHash, String telefone, String endereco, String cpf) {
        super(nome, email, senhaHash, telefone, endereco);
        this.cpf = cpf;
    }

    @Override
    public String getIdentificador() {
        return this.cpf;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}