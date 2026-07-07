package main.java.repository;

import main.java.model.Cliente;
import main.java.model.ItemCardapio;
import main.java.model.Pedido;
import main.java.model.StatusPedido;
import main.java.repository.ItemCardapioRepository;
import main.java.repository.PedidoRepository;
import exception.CancelamentoNaoPermitidoException;
import exception.StatusInvalidoException;

import java.util.List;
import java.util.Scanner;

public class PedidoService {

    private final Scanner scanner = new Scanner(System.in);
    private final ItemCardapioRepository itemRepo = new ItemCardapioRepository();
    private final PedidoRepository pedidoRepo = new PedidoRepository();

    public void fazerPedido(Cliente cliente) {
        System.out.println("\n🍽️ FAZER PEDIDO");
        System.out.println("=".repeat(50));

        List<ItemCardapio> itensDisponiveis = itemRepo.listarTodos().stream()
                .filter(ItemCardapio::isDisponivel)
                .toList();

        if (itensDisponiveis.isEmpty()) {
            System.out.println("❌ Nenhum item disponível no cardápio no momento.");
            return;
        }

        // Criar novo pedido
        Pedido pedido = new Pedido();
        pedido.setId(pedidoRepo.gerarNovoId());
        pedido.setCliente(cliente);

        boolean continuar = true;
        while (continuar) {
            exibirCardapio(itensDisponiveis);
            System.out.println("\n📋 OPÇÕES:");
            System.out.println("  0 - Finalizar pedido");
            System.out.println("  " + (itensDisponiveis.size() + 1) + " - Cancelar pedido");
            System.out.print("\n👉 Digite o número do item desejado: ");

            int opcao;
            while (!scanner.hasNextInt()) {
                System.out.print("❌ Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 0) {
                continuar = false;
            } else if (opcao == itensDisponiveis.size() + 1) {
                System.out.println("\n❌ Pedido cancelado.");
                return;
            } else if (opcao >= 1 && opcao <= itensDisponiveis.size()) {
                ItemCardapio itemSelecionado = itensDisponiveis.get(opcao - 1);

                System.out.print("Quantidade: ");
                int quantidade;
                while (!scanner.hasNextInt()) {
                    System.out.print("❌ Digite um número válido: ");
                    scanner.next();
                }
                quantidade = scanner.nextInt();
                scanner.nextLine();

                if (quantidade <= 0) {
                    System.out.println("❌ Quantidade inválida!");
                    continue;
                }

                // Adicionar item ao pedido (múltiplas vezes)
                for (int i = 0; i < quantidade; i++) {
                    pedido.adicionarItem(itemSelecionado);
                }

                System.out.println("✅ " + quantidade + "x " + itemSelecionado.getNome() + " adicionado ao pedido!");
                System.out.println("💰 Total atual: R$ " + String.format("%.2f", pedido.getValorTotal()));
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }

        // Finalizar pedido
        if (pedido.getItens().isEmpty()) {
            System.out.println("\n❌ Pedido vazio! Nenhum item selecionado.");
            return;
        }

        // Perguntar observações
        System.out.print("\n📝 Observações (opcional): ");
        String observacoes = scanner.nextLine().trim();

        // Salvar pedido
        pedidoRepo.salvar(pedido);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("✅ PEDIDO REALIZADO COM SUCESSO!");
        System.out.println("=".repeat(50));
        System.out.println("📌 Número do pedido: #" + pedido.getId());
        System.out.println("👤 Cliente: " + cliente.getNome());
        System.out.println("📅 Data: " + pedido.getDataHora());
        System.out.println("📦 Status: " + pedido.getStatus());

        System.out.println("\n📋 ITENS DO PEDIDO:");
        System.out.println("-".repeat(40));
        for (ItemCardapio item : pedido.getItens()) {
            System.out.println("  • " + item.getNome() + " - R$ " + String.format("%.2f", item.getPreco()));
        }
        System.out.println("-".repeat(40));
        System.out.println("💰 TOTAL: R$ " + String.format("%.2f", pedido.getValorTotal()));

        if (!observacoes.isEmpty()) {
            System.out.println("📝 Observações: " + observacoes);
        }
        System.out.println("=".repeat(50));
    }

    private void exibirCardapio(List<ItemCardapio> itens) {
        System.out.println("\n📋 CARDÁPIO DISPONÍVEL");
        System.out.println("=".repeat(50));
        System.out.printf("%-5s %-30s %-15s %s%n", "Nº", "Nome", "Categoria", "Preço");
        System.out.println("-".repeat(50));

        int numero = 1;
        for (ItemCardapio item : itens) {
            System.out.printf("%-5d %-30s %-15s R$ %-8.2f%n",
                    numero,
                    item.getNome(),
                    item.getCategoria() != null ? item.getCategoria().getName() : "Sem categoria",
                    item.getPreco());
            numero++;
        }
        System.out.println("=".repeat(50));
    }
}