package repository;

import main.java.model.Restaurante;
import main.java.model.Gerente;
import main.java.util.HashSenha;
import main.java.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestauranteRepository {
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/restaurante.json";
    private final Gson gson;
    private List<Gerente> gerentes;

    public RestauranteRepository() {
        // Registrar o adapter para LocalDateTime
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.gerentes = new ArrayList<>();
        criarArquivoSeNaoExistir();
        carregarGerentes();
    }

    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                arquivo.getParentFile().mkdirs();
                arquivo.createNewFile();
                // Criar um restaurante padrão
                Restaurante restaurantePadrao = new Restaurante();
                restaurantePadrao.setNome("RangoJava Restaurante");
                restaurantePadrao.setEndereco("Rua Principal, 123 - Centro");
                restaurantePadrao.setTelefone("(11) 99999-9999");
                restaurantePadrao.setEmail("contato@rangojava.com");
                restaurantePadrao.setHorarioFuncionamento("Seg-Sex: 11h-23h | Sáb-Dom: 12h-22h");
                restaurantePadrao.setDescricao("O melhor restaurante da cidade!");
                salvar(restaurantePadrao);
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de restaurante: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void carregarGerentes() {
        // Carregar gerentes de um arquivo ou criar um padrão
        // Por enquanto, vamos criar um gerente padrão para teste
        Gerente gerentePadrao = new Gerente();
        gerentePadrao.setId(1);
        gerentePadrao.setNome("Administrador");
        gerentePadrao.setEmail("admin@rangojava.com");
        gerentePadrao.setSenhaHash(HashSenha.gerarHash("admin123"));
        gerentePadrao.setCargo("Gerente Geral");
        gerentePadrao.setCpf("11111111111");
        gerentes.add(gerentePadrao);
    }

    public void salvar(Restaurante restaurante) {
        if (restaurante == null) {
            throw new IllegalArgumentException("Restaurante não pode ser nulo.");
        }

        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(restaurante, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar restaurante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Restaurante carregar() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return null;
        }

        try (Reader reader = new FileReader(arquivo)) {
            return gson.fromJson(reader, Restaurante.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar restaurante: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean existeRestauranteCadastrado() {
        Restaurante restaurante = carregar();
        return restaurante != null && restaurante.getNome() != null && !restaurante.getNome().isEmpty();
    }

    public Gerente buscarGerentePorEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        // Buscar na lista de gerentes
        for (Gerente gerente : gerentes) {
            if (gerente.getEmail() != null && gerente.getEmail().equalsIgnoreCase(email)) {
                return gerente;
            }
        }
        return null;
    }

    public void adicionarGerente(Gerente gerente) {
        if (gerente != null) {
            // Verificar se já existe gerente com mesmo email
            boolean existe = gerentes.stream()
                    .anyMatch(g -> g.getEmail() != null && g.getEmail().equalsIgnoreCase(gerente.getEmail()));

            if (existe) {
                throw new IllegalStateException("Já existe um gerente com este e-mail.");
            }

            gerentes.add(gerente);
        }
    }

    public List<Gerente> listarGerentes() {
        return new ArrayList<>(gerentes);
    }

    public void removerGerente(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        }

        boolean removido = gerentes.removeIf(g -> g.getEmail() != null && g.getEmail().equalsIgnoreCase(email));

        if (!removido) {
            throw new IllegalStateException("Gerente com e-mail " + email + " não encontrado.");
        }
    }

    public Gerente buscarGerentePorId(int id) {
        return gerentes.stream()
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void atualizarGerente(Gerente gerente) {
        if (gerente == null) {
            throw new IllegalArgumentException("Gerente não pode ser nulo.");
        }

        for (int i = 0; i < gerentes.size(); i++) {
            if (gerentes.get(i).getId() == gerente.getId()) {
                gerentes.set(i, gerente);
                return;
            }
        }
        throw new IllegalStateException("Gerente com ID " + gerente.getId() + " não encontrado.");
    }

    public void atualizarRestaurante(Restaurante restaurante) {
        if (restaurante == null) {
            throw new IllegalArgumentException("Restaurante não pode ser nulo.");
        }
        salvar(restaurante);
    }

    public void deletarArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    public boolean arquivoValido() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return false;
        }

        try (Reader reader = new FileReader(arquivo)) {
            Restaurante restaurante = gson.fromJson(reader, Restaurante.class);
            return restaurante != null;
        } catch (IOException e) {
            return false;
        }
    }

    public String getCaminhoArquivo() {
        return CAMINHO_ARQUIVO;
    }

    public long getTamanhoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        return arquivo.exists() ? arquivo.length() : 0;
    }

    // Método para criar restaurante com dados personalizados
    public void criarRestaurantePersonalizado(String nome, String endereco, String telefone, String email) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(nome);
        restaurante.setEndereco(endereco);
        restaurante.setTelefone(telefone);
        restaurante.setEmail(email);
        restaurante.setHorarioFuncionamento("Seg-Sex: 11h-23h | Sáb-Dom: 12h-22h");
        restaurante.setDescricao("Restaurante " + nome);
        salvar(restaurante);
    }

    // Método para contar gerentes
    public int contarGerentes() {
        return gerentes.size();
    }

    // Método para limpar gerentes
    public void limparGerentes() {
        gerentes.clear();
    }

    // Método para adicionar gerente padrão
    public void criarGerentePadrao() {
        if (buscarGerentePorEmail("admin@rangojava.com") == null) {
            Gerente admin = new Gerente();
            admin.setId(1);
            admin.setNome("Administrador");
            admin.setEmail("admin@rangojava.com");
            admin.setSenhaHash(HashSenha.gerarHash("admin123"));
            admin.setCargo("Gerente Geral");
            admin.setCpf("11111111111");
            gerentes.add(admin);
        }
    }
}