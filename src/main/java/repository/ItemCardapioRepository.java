package repository;

import main.java.model.ItemCardapio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemCardapioRepository implements Repository<ItemCardapio, Integer> {
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/cardapio.json";
    private final Gson gson;

    public ItemCardapioRepository() {
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

    private List<ItemCardapio> carregarLista() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<ItemCardapio>>() {}.getType();
            List<ItemCardapio> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void salvarLista(List<ItemCardapio> lista) {
        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int gerarNovoId() {
        return carregarLista().stream()
                .mapToInt(ItemCardapio::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public void salvar(ItemCardapio item) {
        List<ItemCardapio> lista = carregarLista();
        lista.add(item);
        salvarLista(lista);
    }

    @Override
    public ItemCardapio buscarPorId(Integer id) {
        return carregarLista().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ItemCardapio> listarTodos() {
        return new ArrayList<>(carregarLista());
    }

    @Override
    public void atualizar(ItemCardapio item) {
        List<ItemCardapio> lista = carregarLista();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == item.getId()) {
                lista.set(i, item);
                break;
            }
        }
        salvarLista(lista);
    }

    @Override
    public void excluir(Integer id) {
        List<ItemCardapio> lista = carregarLista();
        lista.removeIf(i -> i.getId() == id);
        salvarLista(lista);
    }
}