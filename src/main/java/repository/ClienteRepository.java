package main.java.Repository;

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
        if (!arquivo.exists() || arquivo.length() == 0) return new ArrayList<>();

        try (Reader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<Cliente>>() {}.getType();
            return gson.fromJson(reader, tipoLista);
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
        List<Cliente> lista = carregarLista();
        lista.add(cliente);
        salvarLista(lista);
    }

    @Override
    public Cliente buscarPorId(String cpf) {
        return carregarLista().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarPorEmail(String email) {
        return carregarLista().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(carregarLista());
    }

    @Override
    public void atualizar(Cliente cliente) {
        List<Cliente> lista = carregarLista();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCpf().equals(cliente.getCpf())) {
                lista.set(i, cliente);
                break;
            }
        }
        salvarLista(lista);
    }

    @Override
    public void excluir(String cpf) {
        List<Cliente> lista = carregarLista();
        lista.removeIf(c -> c.getCpf().equals(cpf));
        salvarLista(lista);
    }
}