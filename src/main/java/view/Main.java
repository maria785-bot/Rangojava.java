package view;

import controller.LoginController;
import exception.DocumentoInvalidoException;
import exception.PrecoInvalidoException;
import exception.SenhaInvalidaException;
import model.CategoriaItem;
import model.Cliente;
import model.Gerente;
import model.ItemCardapio;
import model.Pedido;
import model.Restaurante;
import model.StatusPedido;
import repository.CategoriaItemRepository;
import repository.ClienteRepository;
import repository.ItemCardapioRepository;
import repository.PedidoRepository;
import repository.RestauranteRepository;
import util.HashSenha;
import util.ValidadorDocumentos;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClienteRepository clienteRepo = new ClienteRepository();
    private static final ItemCardapioRepository itemRepo = new ItemCardapioRepository();
    private static final CategoriaItemRepository categoriaRepo = new CategoriaItemRepository();
    private static final RestauranteRepository restauranteRepo = new RestauranteRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("       BEM-VINDO AO RANGOJAVA - SISTEMA DE RESTAURANTE");
        System.out.println("=".repeat(60));
        System.out.println();

        inicializarDados();

        int opcao;
        do {
            System.out.println("\n📋 MENU PRINCIPAL");
            System.out.println("=".repeat(40));
            System.out.println("1. 🔐 Fazer Login");
            System.out.println("2. 📝 Cadastrar Cliente");
            System.out.println("3. 🍽️ Ver Cardápio");
            System.out.println("4. 📦 Ver Restaurante");
            System.out.println("5. 📋 Gerenciar Cardápio (Gerente)");
            System.out.println("6. 🚪 Sair");
            System.out.println("=".repeat(40));
            System.out.print(" Escolha uma opção: ");

            while (!scanner.hasNextInt()) {
                System.out.print(" Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    fazerLogin();
                    break;
                case 2:
                    cadastrarCliente();
                    break;
                case 3:
                    verCardapio();
                    break;
                case 4:
                    verRestaurante();
                    break;
                case 5:
                    gerenciarCardapio();
                    break;
                case 6:
                    System.out.println("\n Saindo... Obrigado por usar o RangoJava!");
                    break;
                default:
                    System.out.println("\n Opção inválida! Tente novamente.");
            }

        } while (opcao != 6);

        scanner.close();
    }

    private static void fazerLogin() {
        LoginController loginController = new LoginController();
        loginController.iniciarLogin();
    }

    private static void cadastrarCliente() {
        System.out.println("\n📝 CADASTRO DE CLIENTE");
        System.out.println("=".repeat(40));

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine().trim();

        System.out.print("CPF (apenas números): ");
        String cpf = scanner.nextLine().trim();

        System.out.print("Senha (mínimo 8 caracteres): ");
        String senha = scanner.nextLine().trim();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            System.out.println("\n❌ Todos os campos são obrigatórios!");
            return;
        }

        // ==========================================================
        // VALIDAÇÃO DE CPF - IMPEDE O CADASTRO DE CPF INVÁLIDO!
        // ==========================================================
        try {
            util.ValidadorDocumentos.validarCPF(cpf);
            System.out.println("✅ CPF válido!");  // ← LINHA DE TESTE
        } catch (exception.DocumentoInvalidoException e) {
            System.out.println("\n❌ " + e.getMessage());
            return;  // ← SAI DO MÉTODO, NÃO CADASTRA!
        }

        // ==========================================================
        // VALIDAÇÃO DE SENHA
        // ==========================================================
        try {
            util.ValidadorDocumentos.validarSenha(senha);
            System.out.println("✅ Senha válida!");  // ← LINHA DE TESTE
        } catch (exception.SenhaInvalidaException e) {
            System.out.println("\n❌ " + e.getMessage());
            return;
        }

        // ==========================================================
        // VERIFICAR SE E-MAIL JÁ EXISTE
        // ==========================================================
        if (clienteRepo.buscarPorEmail(email) != null) {
            System.out.println("\n❌ E-mail já cadastrado!");
            return;
        }

        // ==========================================================
        // VERIFICAR SE CPF JÁ EXISTE
        // ==========================================================
        if (clienteRepo.buscarPorId(cpf) != null) {
            System.out.println("\n❌ CPF já cadastrado!");
            return;
        }

        // ==========================================================
        // SALVAR CLIENTE
        // ==========================================================
        try {
            Cliente cliente = new Cliente();
            cliente.setId(gerarProximoIdCliente());
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setCpf(cpf);
            cliente.setSenhaHash(HashSenha.gerarHash(senha));

            clienteRepo.salvar(cliente);
            System.out.println("\n✅ Cliente cadastrado com sucesso!");
            System.out.println("📌 ID do cliente: " + cliente.getId());

        } catch (Exception e) {
            System.out.println("\n❌ Erro ao cadastrar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verCardapio() {
        System.out.println("\n CARDÁPIO");
        System.out.println("=".repeat(50));

        List<ItemCardapio> itens = itemRepo.listarTodos();

        if (itens.isEmpty()) {
            System.out.println(" Nenhum item cadastrado no cardápio.");
            System.out.println(" Cadastre itens no cardápio (opção 5).");
            return;
        }

        System.out.printf("%-5s %-25s %-15s %-10s %s%n", "ID", "Nome", "Categoria", "Preço", "Status");
        System.out.println("-".repeat(65));

        for (ItemCardapio item : itens) {
            String status = item.isDisponivel() ? "✅" : "❌";
            String categoria = item.getCategoria() != null ? item.getCategoria().getName() : "Sem categoria";
            System.out.printf("%-5d %-25s %-15s R$%-9.2f %s%n",
                    item.getId(),
                    item.getNome().length() > 25 ? item.getNome().substring(0, 22) + "..." : item.getNome(),
                    categoria.length() > 15 ? categoria.substring(0, 12) + "..." : categoria,
                    item.getPreco(),
                    status);
        }
        System.out.println("-".repeat(65));
        System.out.println("Total de itens: " + itens.size());
    }

    private static void verRestaurante() {
        System.out.println("\n RESTAURANTE");
        System.out.println("=".repeat(40));

        Restaurante restaurante = restauranteRepo.carregar();

        if (restaurante == null || restaurante.getNome() == null) {
            System.out.println("️ Nenhum restaurante cadastrado.");
            System.out.println(" Configure as informações do restaurante.");
            return;
        }

        System.out.println("🏠 Nome: " + restaurante.getNome());
        System.out.println("📍 Endereço: " + restaurante.getEndereco());
        System.out.println("📞 Telefone: " + restaurante.getTelefone());
        System.out.println("📧 E-mail: " + restaurante.getEmail());

        if (restaurante.getHorarioFuncionamento() != null) {
            System.out.println(" Horário: " + restaurante.getHorarioFuncionamento());
        }

        if (restaurante.getDescricao() != null) {
            System.out.println(" Descrição: " + restaurante.getDescricao());
        }

        System.out.println("\n Estatísticas:");
        System.out.println("   • Itens no cardápio: " + itemRepo.listarTodos().size());
        System.out.println("   • Clientes cadastrados: " + clienteRepo.listarTodos().size());
        System.out.println("   • Categorias: " + categoriaRepo.listarTodos().size());
        System.out.println("   • Pedidos realizados: " + pedidoRepo.listarTodos().size());
    }

    private static void gerenciarCardapio() {
        int opcao;
        do {
            System.out.println("\n📋 GERENCIAR CARDÁPIO");
            System.out.println("=".repeat(40));
            System.out.println("1. 📄 Ver cardápio completo");
            System.out.println("2. ➕ Adicionar item");
            System.out.println("3. ✏️ Editar item");
            System.out.println("4. ❌ Remover item");
            System.out.println("5. 🔄 Alternar disponibilidade");
            System.out.println("6. 📁 Gerenciar categorias");
            System.out.println("7. 🔙 Voltar");
            System.out.print("\n👉 Escolha uma opção: ");

            while (!scanner.hasNextInt()) {
                System.out.print(" Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

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
                    gerenciarCategorias();
                    break;
                case 7:
                    System.out.println(" Voltando...");
                    break;
                default:
                    System.out.println(" Opção inválida!");
            }

        } while (opcao != 7);
    }

    private static void verCardapioCompleto() {
        System.out.println("\n CARDÁPIO COMPLETO");
        System.out.println("=".repeat(60));

        List<ItemCardapio> itens = itemRepo.listarTodos();

        if (itens.isEmpty()) {
            System.out.println(" Nenhum item cadastrado.");
            return;
        }

        System.out.printf("%-5s %-25s %-20s %-10s %s%n", "ID", "Nome", "Categoria", "Preço", "Status");
        System.out.println("-".repeat(70));

        for (ItemCardapio item : itens) {
            String status = item.isDisponivel() ? " Disponível" : " Indisponível";
            String categoria = item.getCategoria() != null ? item.getCategoria().getName() : "Sem categoria";
            System.out.printf("%-5d %-25s %-20s R$%-9.2f %s%n",
                    item.getId(),
                    item.getNome().length() > 25 ? item.getNome().substring(0, 22) + "..." : item.getNome(),
                    categoria.length() > 20 ? categoria.substring(0, 17) + "..." : categoria,
                    item.getPreco(),
                    status);
        }
        System.out.println("-".repeat(70));
        System.out.println("Total: " + itens.size() + " itens");
    }

    private static void adicionarItemCardapio() {
        System.out.println("\n➕ ADICIONAR ITEM AO CARDÁPIO");
        System.out.println("=".repeat(40));

        List<CategoriaItem> categorias = categoriaRepo.listarTodos();
        if (categorias.isEmpty()) {
            System.out.println(" Nenhuma categoria cadastrada!");
            System.out.println(" Cadastre uma categoria primeiro (opção 6).");
            return;
        }

        System.out.println("\n Categorias disponíveis:");
        for (CategoriaItem cat : categorias) {
            System.out.println("   " + cat.getId() + " - " + cat.getName());
        }

        System.out.print("\nID da categoria: ");
        int categoriaId;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        categoriaId = scanner.nextInt();
        scanner.nextLine();

        CategoriaItem categoria = categoriaRepo.buscarPorId(categoriaId);
        if (categoria == null) {
            System.out.println(" Categoria não encontrada!");
            return;
        }

        System.out.print("Nome do item: ");
        String nome = scanner.nextLine().trim();

        if (nome.isEmpty()) {
            System.out.println(" Nome não pode ser vazio!");
            return;
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        System.out.print("Preço: R$ ");
        double preco;
        while (!scanner.hasNextDouble()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        preco = scanner.nextDouble();
        scanner.nextLine();

        if (preco <= 0) {
            System.out.println(" Preço deve ser maior que zero!");
            return;
        }

        System.out.print("Disponível? (S/N): ");
        String disponivelStr = scanner.nextLine().trim().toUpperCase();
        boolean disponivel = disponivelStr.equals("S");

        try {
            int novoId = itemRepo.gerarNovoId();
            ItemCardapio item = new ItemCardapio(novoId, nome, descricao, preco, categoria);
            item.setDisponivel(disponivel);
            itemRepo.salvar(item);

            System.out.println("\n✅ Item adicionado com sucesso!");
            System.out.println("📌 ID: " + novoId);
            System.out.println("🍽️ " + nome + " - R$ " + String.format("%.2f", preco));
            System.out.println("📁 Categoria: " + categoria.getName());
            System.out.println("📊 Status: " + (disponivel ? "Disponível" : "Indisponível"));

        } catch (PrecoInvalidoException e) {
            System.out.println(" Erro ao adicionar item: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(" Erro ao adicionar item: " + e.getMessage());
        }
    }

    private static void editarItemCardapio() {
        System.out.println("\n✏ EDITAR ITEM");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("\nID do item para editar: ");
        int id;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        id = scanner.nextInt();
        scanner.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println(" Item não encontrado!");
            return;
        }

        System.out.println("\nEditando: " + item.getNome());
        System.out.println("(Deixe em branco para manter o valor atual)");
        System.out.println("-".repeat(30));

        System.out.print("Novo nome (" + item.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) item.setNome(nome);

        System.out.print("Nova descrição (" + item.getDescricao() + "): ");
        String descricao = scanner.nextLine().trim();
        if (!descricao.isEmpty()) item.setDescricao(descricao);

        System.out.print("Novo preço (R$ " + String.format("%.2f", item.getPreco()) + "): ");
        String precoStr = scanner.nextLine().trim();
        if (!precoStr.isEmpty()) {
            try {
                double preco = Double.parseDouble(precoStr.replace(",", "."));
                if (preco > 0) {
                    item.setPreco(preco);
                } else {
                    System.out.println(" Preço deve ser maior que zero!");
                }
            } catch (NumberFormatException | PrecoInvalidoException e) {
                System.out.println(" Preço inválido!");
            }
        }

        List<CategoriaItem> categorias = categoriaRepo.listarTodos();
        if (!categorias.isEmpty()) {
            System.out.println("\n Categorias disponíveis:");
            for (CategoriaItem cat : categorias) {
                System.out.println("   " + cat.getId() + " - " + cat.getName());
            }
            System.out.print("Nova categoria (ID) (" + item.getCategoria().getName() + "): ");
            String catIdStr = scanner.nextLine().trim();
            if (!catIdStr.isEmpty()) {
                try {
                    int catId = Integer.parseInt(catIdStr);
                    CategoriaItem novaCategoria = categoriaRepo.buscarPorId(catId);
                    if (novaCategoria != null) {
                        item.setCategoria(novaCategoria);
                    } else {
                        System.out.println(" Categoria não encontrada!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" ID inválido!");
                }
            }
        }

        itemRepo.atualizar(item);
        System.out.println("\n Item atualizado com sucesso!");
    }

    private static void removerItemCardapio() {
        System.out.println("\n REMOVER ITEM");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("\nID do item para remover: ");
        int id;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        id = scanner.nextInt();
        scanner.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println(" Item não encontrado!");
            return;
        }

        System.out.println("\n🗑 Item: " + item.getNome());
        System.out.println("   Preço: R$ " + String.format("%.2f", item.getPreco()));
        System.out.println("   Categoria: " + item.getCategoria().getName());

        System.out.print("\nTem certeza que deseja remover? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            itemRepo.excluir(id);
            System.out.println("\n Item removido com sucesso!");
        } else {
            System.out.println("\n Operação cancelada.");
        }
    }

    private static void alternarDisponibilidade() {
        System.out.println("\n ALTERNAR DISPONIBILIDADE");
        System.out.println("=".repeat(40));

        verCardapioCompleto();

        System.out.print("\nID do item: ");
        int id;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        id = scanner.nextInt();
        scanner.nextLine();

        ItemCardapio item = itemRepo.buscarPorId(id);
        if (item == null) {
            System.out.println(" Item não encontrado!");
            return;
        }

        String statusAtual = item.isDisponivel() ? " DISPONÍVEL" : " INDISPONÍVEL";
        System.out.println("\n Item: " + item.getNome());
        System.out.println(" Status atual: " + statusAtual);

        boolean novoStatus = !item.isDisponivel();
        item.setDisponivel(novoStatus);
        itemRepo.atualizar(item);

        String novoStatusStr = novoStatus ? " DISPONÍVEL" : " INDISPONÍVEL";
        System.out.println("\n Status alterado para: " + novoStatusStr);
    }

    private static void gerenciarCategorias() {
        int opcao;
        do {
            System.out.println("\n📁 GERENCIAR CATEGORIAS");
            System.out.println("=".repeat(40));
            System.out.println("1. 📄 Ver categorias");
            System.out.println("2. ➕ Adicionar categoria");
            System.out.println("3. ✏️ Editar categoria");
            System.out.println("4. ❌ Remover categoria");
            System.out.println("5. 🔙 Voltar");
            System.out.print("\n👉 Escolha uma opção: ");

            while (!scanner.hasNextInt()) {
                System.out.print(" Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    verCategorias();
                    break;
                case 2:
                    adicionarCategoria();
                    break;
                case 3:
                    editarCategoria();
                    break;
                case 4:
                    removerCategoria();
                    break;
                case 5:
                    System.out.println(" Voltando...");
                    break;
                default:
                    System.out.println(" Opção inválida!");
            }

        } while (opcao != 5);
    }

    private static void verCategorias() {
        System.out.println("\n CATEGORIAS");
        System.out.println("=".repeat(40));

        List<CategoriaItem> categorias = categoriaRepo.listarTodos();

        if (categorias.isEmpty()) {
            System.out.println(" Nenhuma categoria cadastrada.");
            return;
        }

        System.out.printf("%-5s %-25s %-30s%n", "ID", "Nome", "Descrição");
        System.out.println("-".repeat(60));

        for (CategoriaItem cat : categorias) {
            String desc = cat.getDescricao() != null ? cat.getDescricao() : "";
            System.out.printf("%-5d %-25s %-30s%n",
                    cat.getId(),
                    cat.getName().length() > 25 ? cat.getName().substring(0, 22) + "..." : cat.getName(),
                    desc.length() > 30 ? desc.substring(0, 27) + "..." : desc);
        }
        System.out.println("-".repeat(60));
        System.out.println("Total: " + categorias.size() + " categorias");
    }

    private static void adicionarCategoria() {
        System.out.println("\n ADICIONAR CATEGORIA");
        System.out.println("=".repeat(40));

        System.out.print("Nome da categoria: ");
        String nome = scanner.nextLine().trim();

        if (nome.isEmpty()) {
            System.out.println(" Nome não pode ser vazio!");
            return;
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        try {
            int novoId = categoriaRepo.gerarNovoId();
            CategoriaItem categoria = new CategoriaItem(novoId, nome, descricao);
            categoriaRepo.salvar(categoria);

            System.out.println("\n Categoria adicionada com sucesso!");
            System.out.println(" ID: " + novoId);
            System.out.println(" " + nome);

        } catch (Exception e) {
            System.out.println(" Erro ao adicionar categoria: " + e.getMessage());
        }
    }

    private static void editarCategoria() {
        System.out.println("\n️ EDITAR CATEGORIA");
        System.out.println("=".repeat(40));

        verCategorias();

        System.out.print("\nID da categoria para editar: ");
        int id;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        id = scanner.nextInt();
        scanner.nextLine();

        CategoriaItem categoria = categoriaRepo.buscarPorId(id);
        if (categoria == null) {
            System.out.println(" Categoria não encontrada!");
            return;
        }

        System.out.println("\nEditando: " + categoria.getName());
        System.out.println("(Deixe em branco para manter o valor atual)");

        System.out.print("Novo nome (" + categoria.getName() + "): ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) categoria.setName(nome);

        System.out.print("Nova descrição (" + categoria.getDescricao() + "): ");
        String descricao = scanner.nextLine().trim();
        if (!descricao.isEmpty()) categoria.setDescricao(descricao);

        categoriaRepo.atualizar(categoria);
        System.out.println("\n Categoria atualizada com sucesso!");
    }

    private static void removerCategoria() {
        System.out.println("\n REMOVER CATEGORIA");
        System.out.println("=".repeat(40));

        verCategorias();

        System.out.print("\nID da categoria para remover: ");
        int id;
        while (!scanner.hasNextInt()) {
            System.out.print(" Digite um número válido: ");
            scanner.next();
        }
        id = scanner.nextInt();
        scanner.nextLine();

        CategoriaItem categoria = categoriaRepo.buscarPorId(id);
        if (categoria == null) {
            System.out.println(" Categoria não encontrada!");
            return;
        }

        List<ItemCardapio> itens = itemRepo.listarTodos();
        boolean temItens = itens.stream()
                .anyMatch(i -> i.getCategoria() != null && i.getCategoria().getId() == id);

        if (temItens) {
            System.out.println("️ Esta categoria possui itens associados!");
            System.out.println(" Não é possível remover uma categoria com itens.");
            return;
        }

        System.out.println("\n🗑 Categoria: " + categoria.getName());
        System.out.print("Tem certeza que deseja remover? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            categoriaRepo.excluir(id);
            System.out.println("\n Categoria removida com sucesso!");
        } else {
            System.out.println("\n Operação cancelada.");
        }
    }

    private static void inicializarDados() {
        if (categoriaRepo.listarTodos().isEmpty()) {
            System.out.println(" Criando categorias padrão...");
            categoriaRepo.criarCategoriasPadrao();
        }

        if (!restauranteRepo.existeRestauranteCadastrado()) {
            System.out.println(" Criando restaurante padrão...");
            Restaurante restaurante = new Restaurante();
            restaurante.setNome("RangoJava Restaurante");
            restaurante.setEndereco("Rua Principal, 123 - Centro");
            restaurante.setTelefone("(11) 99999-9999");
            restaurante.setEmail("contato@rangojava.com");
            restaurante.setHorarioFuncionamento("Seg-Sex: 11h-23h | Sáb-Dom: 12h-22h");
            restaurante.setDescricao("O melhor restaurante da cidade!");
            restauranteRepo.salvar(restaurante);
        }

        if (clienteRepo.listarTodos().isEmpty()) {
            System.out.println(" Criando cliente administrador padrão...");
            try {
                Cliente admin = new Cliente();
                admin.setId(1);
                admin.setNome("Administrador");
                admin.setEmail("admin@rangojava.com");
                admin.setTelefone("(11) 99999-9999");
                admin.setCpf("11111111111");
                admin.setSenhaHash(HashSenha.gerarHash("admin123"));
                clienteRepo.salvar(admin);
            } catch (Exception e) {
                System.err.println("Erro ao criar admin: " + e.getMessage());
            }
        }

        if (itemRepo.listarTodos().isEmpty()) {
            System.out.println(" Criando itens de cardápio padrão...");
            try {
                List<CategoriaItem> categorias = categoriaRepo.listarTodos();

                if (!categorias.isEmpty()) {
                    CategoriaItem bebidas = categorias.stream()
                            .filter(c -> c.getName().equalsIgnoreCase("Bebidas"))
                            .findFirst()
                            .orElse(categorias.get(0));

                    CategoriaItem lanches = categorias.stream()
                            .filter(c -> c.getName().equalsIgnoreCase("Lanches"))
                            .findFirst()
                            .orElse(categorias.get(0));

                    CategoriaItem sobremesas = categorias.stream()
                            .filter(c -> c.getName().equalsIgnoreCase("Sobremesas"))
                            .findFirst()
                            .orElse(categorias.get(0));

                    ItemCardapio item1 = new ItemCardapio(1, "Café Expresso", "Café forte e encorpado", 5.50, bebidas);
                    itemRepo.salvar(item1);

                    ItemCardapio item2 = new ItemCardapio(2, "Pão de Queijo", "Pão de queijo mineiro", 8.00, lanches);
                    itemRepo.salvar(item2);

                    ItemCardapio item3 = new ItemCardapio(3, "Suco Natural", "Suco de frutas frescas", 7.50, bebidas);
                    itemRepo.salvar(item3);

                    ItemCardapio item4 = new ItemCardapio(4, "Tiramisu", "Sobremesa italiana", 15.00, sobremesas);
                    itemRepo.salvar(item4);
                }
            } catch (Exception e) {
                System.err.println("Erro ao criar itens padrão: " + e.getMessage());
            }
        }
    }

    private static int gerarProximoIdCliente() {
        return clienteRepo.listarTodos().stream()
                .mapToInt(Cliente::getId)
                .max()
                .orElse(0) + 1;
    }
}
