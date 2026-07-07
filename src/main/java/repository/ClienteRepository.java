package repository;

import model.Cliente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository implements Repository<Cliente, String> {
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/clientes.json";
    private final Gson gson;

    public ClienteRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        criarArquivoSeNaoExistir();
    }

    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                arquivo.getParentFile().mkdirs();
                arquivo.createNewFile();
                salvarLista(new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Cliente> carregarLista() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<Cliente>>() {}.getType();
            List<Cliente> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void salvarLista(List<Cliente> lista) {
        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        String cpf = cliente.getCpf();
        if (cpf != null && buscarPorId(cpf) != null) {
            throw new IllegalStateException("Cliente com CPF " + cpf + " já existe.");
        }
        List<Cliente> lista = carregarLista();
        lista.add(cliente);
        salvarLista(lista);
    }

    @Override
    public Cliente buscarPorId(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }
        return carregarLista().stream()
                .filter(c -> c.getCpf() != null && c.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return carregarLista().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return carregarLista().stream()
                .filter(c -> c.getNome() != null && c.getNome().toLowerCase().contains(nome.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(carregarLista());
    }

    @Override
    public void atualizar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        String cpf = cliente.getCpf();
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente não pode ser vazio.");
        }
        List<Cliente> lista = carregarLista();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCpf() != null && lista.get(i).getCpf().equals(cpf)) {
                lista.set(i, cliente);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            throw new IllegalStateException("Cliente com CPF " + cpf + " não encontrado.");
        }
        salvarLista(lista);
    }

    @Override
    public void excluir(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        List<Cliente> lista = carregarLista();
        boolean removido = lista.removeIf(c -> c.getCpf() != null && c.getCpf().equals(cpf));
        if (!removido) {
            throw new IllegalStateException("Cliente com CPF " + cpf + " não encontrado.");
        }
        salvarLista(lista);
    }

    public void limparDados() {
        salvarLista(new ArrayList<>());
    }

    public long contarClientes() {
        return carregarLista().size();
    }

    public boolean existeCliente(String cpf) {
        return buscarPorId(cpf) != null;
    }
}
