package controller;

import java.util.Scanner;
import model.Cliente;
import model.Gerente;
import model.ItemCardapio;
import model.Pedido;
import model.StatusPedido;
import repository.ClienteRepository;
import repository.ItemCardapioRepository;
import repository.PedidoRepository;
import repository.RestauranteRepository;
import util.HashSenha;
import exception.CancelamentoNaoPermitidoException;
import exception.StatusInvalidoException;

import java.util.List;

public class LoginController {

    private final Scanner entrada = new Scanner(System.in);
    private final ClienteRepository clienteRepo = new ClienteRepository();
    private final RestauranteRepository restauranteRepo = new RestauranteRepository();
    private final ItemCardapioRepository itemRepo = new ItemCardapioRepository();
    private final PedidoRepository pedidoRepo = new PedidoRepository();

    public void iniciarLogin() {
        System.out.println("=".repeat(50));
        System.out.println("        🍽️  SISTEMA DE LOGIN - RangoJava");
        System.out.println("=".repeat(50));
        System.out.println();

        System.out.print("📧 Digite seu e-mail: ");
        String email = entrada.nextLine().trim();

        System.out.print("🔒 Digite sua senha: ");
        String senhaDigitada = entrada.nextLine().trim();

        if (email.isEmpty() || senhaDigitada.isEmpty()) {
            System.out.println("\n❌ Preencha todos os campos!");
            return;
        }

        try {
            Cliente cliente = clienteRepo.buscarPorEmail(email);
            if (cliente != null && HashSenha.verificarSenha(senhaDigitada, cliente.getSenhaHash())) {
                System.out.println("\n✅ Login realizado com sucesso!");
                System.out.println("👋 Bem-vindo(a), " + cliente.getNome() + "!");
                menuCliente(cliente);
                return;
            }

            Gerente gerente = restauranteRepo.buscarGerentePorEmail(email);
            if (gerente != null && HashSenha.verificarSenha(senhaDigitada, gerente.getSenhaHash())) {
                System.out.println("\n✅ Login realizado com sucesso!");
                System.out.println("👋 Bem-vindo(a), " + gerente.getNome() + "!");
                menuGerente(gerente);
                return;
            }

            System.out.println("\n❌ E-mail ou senha incorretos!");

        } catch (Exception e) {
            System.out.println("\n❌ Erro no login: " + e.getMessage());
        }
    }

    private void menuCliente(Cliente cliente) {
        int opcao;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("        📋 MENU CLIENTE");
            System.out.println("=".repeat(40));
            System.out.println("1. 🍽️ Fazer pedido");
            System.out.println("2. 📜 Ver histórico de pedidos");
            System.out.println("3. 👤 Ver meus dados");
            System.out.println("4. 🔄 Atualizar meus dados");
            System.out.println("5. 🚪 Sair");
            System.out.print("\n👉 Escolha uma opção: ");

            while (!entrada.hasNextInt()) {
                System.out.print("❌ Digite um número válido: ");
                entrada.next();
            }
            opcao = entrada.nextInt();
            entrada.nextLine();

            switch (opcao) {
                case 1:
                    fazerPedido(cliente);
                    break;
                case 2:
                    verHistoricoPedidos(cliente);
                    break;
                case 3:
                    verDadosCliente(cliente);
                    break;
                case 4:
                    atualizarDadosCliente(cliente);
                    break;
                case 5:
                    System.out.println("\n👋 Saindo... Volte sempre!");
                    break;
                default:
                    System.out.println("\n❌ Opção inválida!");
            }

        } while (opcao != 5);
    }

    private void fazerPedido(Cliente cliente) {
        System.out.println("\n🍽️ FAZER PEDIDO");
        System.out.println("=".repeat(50));

        List<ItemCardapio> itensDisponiveis = itemRepo.listarTodos().stream()
                .filter(ItemCardapio::isDisponivel)
                .toList();

        if (itensDisponiveis.isEmpty()) {
            System.out.println("❌ Nenhum item disponível no cardápio no momento.");
            return;
        }

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
            while (!entrada.hasNextInt()) {
                System.out.print("❌ Digite um número válido: ");
                entrada.next();
            }
            opcao = entrada.nextInt();
            entrada.nextLine();

            if (opcao == 0) {
                continuar = false;
            } else if (opcao == itensDisponiveis.size() + 1) {
                System.out.println("\n❌ Pedido cancelado.");
                return;
            } else if (opcao >= 1 && opcao <= itensDisponiveis.size()) {
                ItemCardapio itemSelecionado = itensDisponiveis.get(opcao - 1);

                System.out.print("Quantidade: ");
                int quantidade;
                while (!entrada.hasNextInt()) {
                    System.out.print("❌ Digite um número válido: ");
                    entrada.next();
                }
                quantidade = entrada.nextInt();
                entrada.nextLine();

                if (quantidade <= 0) {
                    System.out.println("❌ Quantidade inválida!");
                    continue;
                }

                for (int i = 0; i < quantidade; i++) {
                    pedido.adicionarItem(itemSelecionado);
                }

                System.out.println("✅ " + quantidade + "x " + itemSelecionado.getNome() + " adicionado ao pedido!");
                System.out.println("💰 Total atual: R$ " + String.format("%.2f", pedido.getValorTotal()));
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("\n❌ Pedido vazio! Nenhum item selecionado.");
            return;
        }

        System.out.print("\n📝 Observações (opcional): ");
        String observacoes = entrada.nextLine().trim();

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

    private void verHistoricoPedidos(Cliente cliente) {
        System.out.println("\n📜 HISTÓRICO DE PEDIDOS");
        System.out.println("=".repeat(50));

        List<Pedido> pedidos = pedidoRepo.listarPorCliente(cliente.getCpf());

        if (pedidos.isEmpty()) {
            System.out.println("📭 Você ainda não fez nenhum pedido.");
            return;
        }

        System.out.println("Total de pedidos: " + pedidos.size());
        System.out.println("-".repeat(50));

        for (Pedido pedido : pedidos) {
            System.out.println("📌 Pedido #" + pedido.getId());
            System.out.println("   Data: " + pedido.getDataHora());
            System.out.println("   Status: " + pedido.getStatus());
            System.out.println("   Itens: " + pedido.getItens().size());
            System.out.println("   Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("-".repeat(30));
        }
    }

    private void verDadosCliente(Cliente cliente) {
        System.out.println("\n👤 DADOS DO CLIENTE");
        System.out.println("=".repeat(40));
        System.out.println("   ID: " + cliente.getId());
        System.out.println("   Nome: " + cliente.getNome());
        System.out.println("   E-mail: " + cliente.getEmail());
        System.out.println("   Telefone: " + cliente.getTelefone());
        System.out.println("   CPF: " + cliente.getCpf());
    }

    private void atualizarDadosCliente(Cliente cliente) {
        System.out.println("\n🔄 ATUALIZAR DADOS");
        System.out.println("=".repeat(40));
        System.out.println("(Deixe em branco para manter o valor atual)");

        System.out.print("Novo nome (" + cliente.getNome() + "): ");
        String nome = entrada.nextLine().trim();
        if (!nome.isEmpty()) cliente.setNome(nome);

        System.out.print("Novo telefone (" + cliente.getTelefone() + "): ");
        String telefone = entrada.nextLine().trim();
        if (!telefone.isEmpty()) cliente.setTelefone(telefone);

        System.out.print("Nova senha (mínimo 8 caracteres): ");
        String senha = entrada.nextLine().trim();
        if (!senha.isEmpty()) {
            if (senha.length() >= 8) {
                cliente.setSenhaHash(HashSenha.gerarHash(senha));
                System.out.println("✅ Senha atualizada com sucesso!");
            } else {
                System.out.println("❌ Senha deve ter no mínimo 8 caracteres!");
            }
        }

        clienteRepo.atualizar(cliente);
        System.out.println("✅ Dados atualizados com sucesso!");
    }

    private void menuGerente(Gerente gerente) {
        int opcao;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("        📋 MENU GERENTE");
            System.out.println("=".repeat(40));
            System.out.println("1. 📋 Gerenciar cardápio");
            System.out.println("2. 📦 Gerenciar pedidos");
            System.out.println("3. 👥 Gerenciar clientes");
            System.out.println("4. 👤 Ver meus dados");
            System.out.println("5. 🚪 Sair");
            System.out.print("\n👉 Escolha uma opção: ");

            while (!entrada.hasNextInt()) {
                System.out.print("❌ Digite um número válido: ");
                entrada.next();
            }
            opcao = entrada.nextInt();
            entrada.nextLine();

            switch (opcao) {
                case 1:
                    gerenciarCardapio();
                    break;
                case 2:
                    gerenciarPedidos();
                    break;
                case 3:
                    gerenciarClientes();
                    break;
                case 4:
                    verDadosGerente(gerente);
                    break;
                case 5:
                    System.out.println("\n👋 Saindo... Volte sempre!");
                    break;
                default:
                    System.out.println("\n❌ Opção inválida!");
            }

        } while (opcao != 5);
    }

    private void gerenciarCardapio() {
        int opcao;
        do {
            System.out.println("\n📋 GERENCIAR CARDÁPIO");
            System.out.println("=".repeat(40));
            System.out.println("1. 📄 Ver cardápio completo");
            System.out.println("2. ➕ Adicionar item");
            System.out.println("3. ✏️ Editar item");
            System.out.println("4. ❌ Remover item");
            System.out.println("5. 🔄 Alternar disponibilidade");
            System.out.println("6. 🔙 Voltar");
            System.out.print("\n👉 Escolha uma opção: ");

            while (!entrada.hasNextInt()) {
                System.out.print("❌ Digite um número válido: ");
                entrada.next();
            }
            opcao = entrada.nextInt();
            entrada.nextLine();

            switch (opcao) {
                case 1:
                    verCardapioCompleto();
                    break;
                case 2:
                    adicionarItemCardapio();
                    break;
                case 3:
                    editarItemCardapio();
                    break;
                case 4:
                    removerItemCardapio();
                    break;
                case 5:
                    alternarDisponibilidade();
                    break;
                case 6:
                    System.out.println("🔙 Voltando...");
                    break;
                default:
                    System.out.println("❌ Opção inválida!");
            }

        } while (opcao != 6);
    }

    private void verCardapioCompleto() {
        System.out.println("\n📄 CARDÁPIO COMPLETO");
        System.out.println("=".repeat(50));

        List<ItemCardapio> itens = itemRepo.listarTodos();

        if (itens.isEmpty()) {
            System.out.println("📭 Nenhum item cadastrado.");
            return;
        }

        System.out.printf("%-5s %-25s %-15s %-10s %s%n", "ID", "Nome", "Categoria", "Preço", "Status");
        System.out.println("-".repeat(60));

        for (ItemCardapio item : itens) {
            String status = item.isDisponivel() ? "✅ Disponível" : "❌ Indisponível";
            System.out.printf("%-5d %-25s %-15s R$%-9.2f %s%n",
                    item.getId(),
                    item.getNome(),
                    item.getCategoria() != null ? item.getCategoria().getName() : "Sem categoria",
                    item.getPreco(),
                    status);
        }
        System.out.println("-".repeat(60));
        System.out.println("Total: " + itens.size() + " itens");
    }

    private void adicionarItemCardapio() {
        System.out.println("\n➕ ADICIONAR ITEM");
        System.out.println("=".repeat(40));

        System.out.print("Nome: ");
        String nome = entrada.nextLine().trim();

        System.out.print("Descrição: ");
        String descricao = entrada.nextLine().trim();

        System.out.print("Preço: ");
        double preco;
        while (!entrada.hasNextDouble()) {
            System.out.print("❌ Digite um número válido: ");
            entrada.next();
        }
        preco = entrada.nextDouble();
        entrada.nextLine();

        System.out.print("Categoria (ID): ");
        int categoriaId;
        while (!entrada.hasNextInt()) {
            System.out.print("❌ Digite um número válido: ");
            entrada.next();
        }
        categoriaId = entrada.nextInt();
        entrada.nextLine();

        try {
            var categoria = new model.CategoriaItem();
            categoria.setId(categoriaId);
            categoria.setNome("Categoria " + categoriaId);

            int novoId = itemRepo.gerarNovoId();
            ItemCardapio item = new ItemCardapio(novoId, nome, descricao, preco, categoria);
            itemRepo.salvar(item);

            System.out.println("✅ Item adicionado com sucesso! ID: " + novoId);
        } catch (Exception e) {
            System.out.println("❌ Erro ao adicionar item: " + e.getMessage());
        }
    }

    private void editarItemCardapio() {
        System.out.println("\n✏️ EDITAR ITEM");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("ID do item para editar: ");
        int id;
        while (!entrada.hasNextInt()) {
            System.out.print("❌ Digite um número válido: ");
            entrada.next();
        }
        id = entrada.nextInt();
        entrada.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println("❌ Item não encontrado!");
            return;
        }

        System.out.println("Editando: " + item.getNome());
        System.out.println("(Deixe em branco para manter o valor atual)");

        System.out.print("Novo nome (" + item.getNome() + "): ");
        String nome = entrada.nextLine().trim();
        if (!nome.isEmpty()) item.setNome(nome);

        System.out.print("Nova descrição (" + item.getDescricao() + "): ");
        String descricao = entrada.nextLine().trim();
        if (!descricao.isEmpty()) item.setDescricao(descricao);

        System.out.print("Novo preço (" + item.getPreco() + "): ");
        String precoStr = entrada.nextLine().trim();
        if (!precoStr.isEmpty()) {
            try {
                double preco = Double.parseDouble(precoStr);
                item.setPreco(preco);
            } catch (Exception e) {
                System.out.println("❌ Preço inválido!");
            }
        }

        itemRepo.atualizar(item);
        System.out.println("✅ Item atualizado com sucesso!");
    }

    private void removerItemCardapio() {
        System.out.println("\n❌ REMOVER ITEM");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("ID do item para remover: ");
        int id;
        while (!entrada.hasNextInt()) {
            System.out.print("❌ Digite um número válido: ");
            entrada.next();
        }
        id = entrada.nextInt();
        entrada.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println("❌ Item não encontrado!");
            return;
        }

        System.out.print("Tem certeza que deseja remover '" + item.getNome() + "'? (S/N): ");
        String confirmacao = entrada.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            itemRepo.excluir(id);
            System.out.println("✅ Item removido com sucesso!");
        } else {
            System.out.println("❌ Operação cancelada.");
        }
    }

    private void alternarDisponibilidade() {
        System.out.println("\n🔄 ALTERNAR DISPONIBILIDADE");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("ID do item: ");
        int id;
        while (!entrada.hasNextInt()) {
            System.out.print("❌ Digite um número válido: ");
            entrada.next();
        }
        id = entrada.nextInt();
        entrada.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println("❌ Item não encontrado!");
            return;
        }

        boolean novoStatus = !item.isDisponivel();
        item.setDisponivel(novoStatus);
        itemRepo.atualizar(item);

        String status = novoStatus ? "✅ DISPONÍVEL" : "❌ INDISPONÍVEL";
        System.out.println("Item '" + item.getNome() + "' agora está " + status);
    }

    private void gerenciarPedidos() {
        System.out.println("\n📦 GERENCIAR PEDIDOS");
        System.out.println("=".repeat(40));

        List<Pedido> pedidos = pedidoRepo.listarTodos();

        if (pedidos.isEmpty()) {
            System.out.println("📭 Nenhum pedido realizado.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-20s %s%n", "ID", "Cliente", "Status", "Data", "Total");
        System.out.println("-".repeat(70));

        for (Pedido pedido : pedidos) {
            String nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "N/A";
            System.out.printf("%-5d %-20s %-15s %-20s R$%-8.2f%n",
                    pedido.getId(),
                    nomeCliente.length() > 20 ? nomeCliente.substring(0, 17) + "..." : nomeCliente,
                    pedido.getStatus(),
                    pedido.getDataHora().toString().substring(0, 16),
                    pedido.getValorTotal());
        }
    }

    private void gerenciarClientes() {
        System.out.println("\n👥 GERENCIAR CLIENTES");
        System.out.println("=".repeat(40));

        List<Cliente> clientes = clienteRepo.listarTodos();

        if (clientes.isEmpty()) {
            System.out.println("📭 Nenhum cliente cadastrado.");
            return;
        }

        System.out.printf("%-5s %-25s %-30s %-15s%n", "ID", "Nome", "E-mail", "Telefone");
        System.out.println("-".repeat(75));

        for (Cliente cliente : clientes) {
            System.out.printf("%-5d %-25s %-30s %-15s%n",
                    cliente.getId(),
                    cliente.getNome().length() > 25 ? cliente.getNome().substring(0, 22) + "..." : cliente.getNome(),
                    cliente.getEmail().length() > 30 ? cliente.getEmail().substring(0, 27) + "..." : cliente.getEmail(),
                    cliente.getTelefone());
        }
        System.out.println("-".repeat(75));
        System.out.println("Total: " + clientes.size() + " clientes");
    }

    private void verDadosGerente(Gerente gerente) {
        System.out.println("\n👤 DADOS DO GERENTE");
        System.out.println("=".repeat(40));
        System.out.println("   ID: " + gerente.getId());
        System.out.println("   Nome: " + gerente.getNome());
        System.out.println("   E-mail: " + gerente.getEmail());
        System.out.println("   Cargo: " + gerente.getCargo());
        System.out.println("   CPF: " + gerente.getCpf());
    }
}
