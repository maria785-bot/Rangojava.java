package main.java.model;

import exception.CancelamentoNaoPermitidoException;
import exception.StatusInvalidoException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;
    private List<ItemCardapio> itens;
    private StatusPedido status;
    private LocalDateTime dataHora;
    private double valorTotal;

    public Pedido() {
        this.itens = new ArrayList<>();
        this.status = StatusPedido.AGUARDANDO_CONFIRMACAO;
        this.dataHora = LocalDateTime.now();
        this.valorTotal = 0.0;
    }

    public void adicionarItem(ItemCardapio item) {
        this.itens.add(item);
        calcularTotal();
    }

    public void removerItem(ItemCardapio item) {
        this.itens.remove(item);
        calcularTotal();
    }

    public void calcularTotal() {
        this.valorTotal = 0;
        for (ItemCardapio item : itens) {
            this.valorTotal += item.getPreco();
        }
    }

    public void setStatus(StatusPedido novoStatus) throws StatusInvalidoException, CancelamentoNaoPermitidoException {
        StatusPedido statusAtual = this.status;

        if (novoStatus == StatusPedido.CANCELADO) {
            if (statusAtual != StatusPedido.AGUARDANDO_CONFIRMACAO) {
                throw new CancelamentoNaoPermitidoException();
            }
        } else if (!podeAvancar(statusAtual, novoStatus)) {
            throw new StatusInvalidoException();
        }

        this.status = novoStatus;
    }

    private boolean podeAvancar(StatusPedido atual, StatusPedido proximo) {
        return switch (atual) {
            case AGUARDANDO_CONFIRMACAO -> proximo == StatusPedido.CONFIRMADO;
            case CONFIRMADO -> proximo == StatusPedido.EM_PREPARO;
            case EM_PREPARO -> proximo == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA -> proximo == StatusPedido.ENTREGUE;
            default -> false;
        };
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<ItemCardapio> getItens() { return new ArrayList<>(itens); }
    public StatusPedido getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getValorTotal() { return valorTotal; }
}