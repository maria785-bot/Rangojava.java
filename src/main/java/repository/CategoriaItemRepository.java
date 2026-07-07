package repository;

import main.java.model.CategoriaItem;  // ← CORRIGIDO: era "CategoriatItem"
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoriaItemRepository implements Repository<CategoriaItem, Integer> {  // ← CORRIGIDO
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/categorias.json";
    private final Gson gson;

    public CategoriaItemRepository() {
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

    private List<CategoriaItem> carregarLista() {  // ← CORRIGIDO
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<CategoriaItem>>() {}.getType();  // ← CORRIGIDO
            List<CategoriaItem> lista = gson.fromJson(reader, tipoLista);  // ← CORRIGIDO
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void salvarLista(List<CategoriaItem> lista) {  // ← CORRIGIDO
        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int gerarNovoId() {
        return carregarLista().stream()
                .mapToInt(CategoriaItem::getId)  // ← CORRIGIDO
                .max()
                .orElse(0) + 1;
    }

    public CategoriaItem buscarPorNome(String nome) {  // ← CORRIGIDO
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        return carregarLista().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(nome.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void salvar(CategoriaItem categoria) {  // ← CORRIGIDO
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        List<CategoriaItem> lista = carregarLista();  // ← CORRIGIDO

        // Verificar se já existe categoria com mesmo nome
        boolean existe = lista.stream()
                .anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(categoria.getName()));

        if (existe) {
            throw new IllegalStateException("Categoria com nome '" + categoria.getName() + "' já existe.");
        }

        lista.add(categoria);
        salvarLista(lista);
    }

    @Override
    public CategoriaItem buscarPorId(Integer id) {  // ← CORRIGIDO
        if (id == null) {
            return null;
        }

        return carregarLista().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CategoriaItem> listarTodos() {  // ← CORRIGIDO
        return new ArrayList<>(carregarLista());
    }

    @Override
    public void atualizar(CategoriaItem categoria) {  // ← CORRIGIDO
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        List<CategoriaItem> lista = carregarLista();  // ← CORRIGIDO
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == categoria.getId()) {
                lista.set(i, categoria);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IllegalStateException("Categoria com ID " + categoria.getId() + " não encontrada.");
        }

        salvarLista(lista);
    }

    @Override
    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        List<CategoriaItem> lista = carregarLista();  // ← CORRIGIDO
        boolean removido = lista.removeIf(c -> c.getId() == id);

        if (!removido) {
            throw new IllegalStateException("Categoria com ID " + id + " não encontrada.");
        }

        salvarLista(lista);
    }

    // Método para verificar se categoria existe
    public boolean existeCategoria(String nome) {
        return buscarPorNome(nome) != null;
    }

    // Método para contar categorias
    public long contarCategorias() {
        return carregarLista().size();
    }

    // Método para limpar todos os dados
    public void limparDados() {
        salvarLista(new ArrayList<>());
    }

    public void criarCategoriasPadrao() {
    }
}