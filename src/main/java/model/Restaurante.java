package main.java.model;

public class Restaurante {
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String categoriaCulinaria;
    private String email;
    private String senhaGerenteHash;
    private String caminhoLogo;

    // Construtor, Getters e Setters
    public Restaurante() {}

    public Restaurante(String nomeFantasia, String cnpj, String endereco, String telefone, String categoriaCulinaria, String email, String senhaGerenteHash) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.categoriaCulinaria = categoriaCulinaria;
        this.email = email;
        this.senhaGerenteHash = senhaGerenteHash;
    }

    // ... Getters e Setters ...
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getSenhaGerenteHash() { return senhaGerenteHash; }
    public void setSenhaGerenteHash(String senhaGerenteHash) { this.senhaGerenteHash = senhaGerenteHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCaminhoLogo() { return caminhoLogo; }
    public void setCaminhoLogo(String caminhoLogo) { this.caminhoLogo = caminhoLogo; }
}
