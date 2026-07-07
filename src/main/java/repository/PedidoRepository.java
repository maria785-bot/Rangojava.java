package main.java.repository;

import main.java.model.Pedido;
import main.java.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository implements Repository<Pedido, Integer> {
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/pedidos.json";
    private final Gson gson;

    public PedidoRepository() {
        // Registrar o adapter para LocalDateTime
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
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

    private List<Pedido> carregarLista() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<Pedido>>() {}.getType();
            List<Pedido> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void salvarLista(List<Pedido> lista) {
        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int gerarNovoId() {
        return carregarLista().stream()
                .mapToInt(Pedido::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public void salvar(Pedido pedido) {
        List<Pedido> lista = carregarLista();
        lista.add(pedido);
        salvarLista(lista);
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return carregarLista().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(carregarLista());
    }

    public List<Pedido> listarPorCliente(String cpfCliente) {
        return carregarLista().stream()
                .filter(p -> p.getCliente() != null &&
                        p.getCliente().getCpf() != null &&
                        p.getCliente().getCpf().equals(cpfCliente))
                .toList();
    }

    @Override
    public void atualizar(Pedido pedido) {
        List<Pedido> lista = carregarLista();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == pedido.getId()) {
                lista.set(i, pedido);
                break;
            }
        }
        salvarLista(lista);
    }

    @Override
    public void excluir(Integer id) {
        List<Pedido> lista = carregarLista();
        lista.removeIf(p -> p.getId() == id);
        salvarLista(lista);
    }
}