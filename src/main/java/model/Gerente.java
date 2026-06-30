package model;

import exception.CancelamentoNaoPermitidoException;
import exception.StatusInvalidoException;

public class Gerente extends Usuario {
    private String cnpjRestaurante;

    public Gerente() {}

    public Gerente(String nome, String email, String senhaHash, String telefone, String endereco, String cnpjRestaurante) {
        super(nome, email, senhaHash, telefone, endereco);
        this.cnpjRestaurante = cnpjRestaurante;
    }

    @Override
    public String getIdentificador() {
        return this.cnpjRestaurante;
    }

    public void alterarStatusPedido(Pedido pedido, StatusPedido novoStatus)
            throws StatusInvalidoException, CancelamentoNaoPermitidoException {
        pedido.setStatus(novoStatus);
    }

    // Getter e Setter se precisar
    public String getCnpjRestaurante() {
        return cnpjRestaurante;
    }

    public void setCnpjRestaurante(String cnpjRestaurante) {
        this.cnpjRestaurante = cnpjRestaurante;
    }
}