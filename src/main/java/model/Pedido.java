package model;

import exception.CancelamentoNaoPermitidoException;
import exception.StatusInvalidoException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Altera o status do pedido com validações de regra de negócio.
 *
 * Regras aplicadas:
 * 1. Não permite cancelar um pedido já entregue
 * 2. Não permite alterar um pedido cancelado
 * 3. Não permite voltar para NOVO após PREPARANDO
 * 4. Não permite voltar para PREPARANDO após PRONTO
 *
 * @throws StatusInvalidoException Se a transição não for permitida
 * @throws CancelamentoNaoPermitidoException Se tentar cancelar pedido entregue
 */

public class Pedido {
    private int id;
    private Cliente cliente;
    private final List<ItemCardapio> itens;  // ← CORRIGIDO: ItemCardapio (maiúsculo)
    private StatusPedido status;
    private final LocalDateTime dataHora;
    private double valorTotal;

    // Construtor vazio
    public Pedido() {
        this.itens = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.NOVO;
        this.valorTotal = 0.0;
    }

    // Construtor com ID e Cliente
    public Pedido(int id, Cliente cliente) {
        this();
        this.id = id;
        this.cliente = cliente;
    }

    // Adiciona um item ao pedido
    public void adicionarItem(ItemCardapio item) {  // ← CORRIGIDO
        if (item != null && item.isDisponivel()) {
            itens.add(item);
            calcularValorTotal();
        }
    }

    // Remove um item do pedido
    public void removerItem(ItemCardapio item) {  // ← CORRIGIDO
        itens.remove(item);
        calcularValorTotal();
    }

    // Altera status com validação
    public void setStatus(StatusPedido novoStatus) throws StatusInvalidoException, CancelamentoNaoPermitidoException {
        if (novoStatus == null) {
            throw new StatusInvalidoException("O status informado não pode ser nulo.");
        }

        // Validação: não permitir cancelar pedido já entregue
        if (this.status == StatusPedido.ENTREGUE && novoStatus == StatusPedido.CANCELADO) {
            throw new CancelamentoNaoPermitidoException("Não é permitido cancelar um pedido já entregue.");
        }

        // Validação: não permitir alterar status de pedido cancelado
        if (this.status == StatusPedido.CANCELADO) {
            throw new StatusInvalidoException("Não é permitido alterar o status de um pedido cancelado.");
        }

        // Validação: não permitir voltar para NOVO depois de PREPARANDO
        if (this.status == StatusPedido.PREPARANDO && novoStatus == StatusPedido.NOVO) {
            throw new StatusInvalidoException("Não é permitido voltar para NOVO após iniciar o preparo.");
        }

        // Validação: não permitir voltar para PREPARANDO depois de PRONTO
        if (this.status == StatusPedido.PRONTO && novoStatus == StatusPedido.PREPARANDO) {
            throw new StatusInvalidoException("Não é permitido voltar para PREPARANDO após estar PRONTO.");
        }

        this.status = novoStatus;
    }

    // Calcula valor total do pedido
    private void calcularValorTotal() {
        valorTotal = 0.0;
        for (ItemCardapio item : itens) {  // ← CORRIGIDO
            valorTotal += item.getPreco();
        }
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemCardapio> getItens() {  // ← CORRIGIDO
        return new ArrayList<>(itens);
    }

    public StatusPedido getStatus() {
        return status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    // Método para verificar se o pedido está vazio
    public boolean isEmpty() {
        return itens.isEmpty();
    }

    // Método para contar itens
    public int getQuantidadeItens() {
        return itens.size();
    }

    // Método para limpar o pedido
    public void limparPedido() {
        itens.clear();
        calcularValorTotal();
    }

    @Override
    public String toString() {
        return "Pedido nº " + id +
                "\nData/Hora: " + dataHora +
                "\nStatus: " + status +
                "\nCliente: " + (cliente != null ? cliente.getNome() : "Não informado") +
                "\nValor Total: R$ " + String.format("%.2f", valorTotal);
    }
}