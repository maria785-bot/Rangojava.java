package repository;

import exception.ArquivoImportacaoException;
import exception.PrecoInvalidoException;
import model.CategoriaItem;
import model.ItemCardapio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ImportacaoCardapioService {
    private final Gson gson;
    private final ItemCardapioRepository itemRepo;
    private final CategoriaItemRepository categoriaRepo;

    public ImportacaoCardapioService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.itemRepo = new ItemCardapioRepository();
        this.categoriaRepo = new CategoriaItemRepository();
    }

    public void importarDoArquivo(File arquivoJson) throws ArquivoImportacaoException {
        if (arquivoJson == null) {
            throw new ArquivoImportacaoException("Arquivo não pode ser nulo.");
        }

        if (!arquivoJson.exists() || arquivoJson.length() == 0) {
            throw new ArquivoImportacaoException("Arquivo ausente ou vazio: " + arquivoJson.getPath());
        }

        if (!arquivoJson.getName().toLowerCase().endsWith(".json")) {
            throw new ArquivoImportacaoException("Arquivo deve ser JSON: " + arquivoJson.getName());
        }

        try (Reader reader = new FileReader(arquivoJson);
             JsonReader jsonReader = new JsonReader(reader)) {

            JsonObject objetoRaiz = gson.fromJson(jsonReader, JsonObject.class);

            if (objetoRaiz == null) {
                throw new ArquivoImportacaoException("Arquivo JSON inválido ou vazio.");
            }

            if (!objetoRaiz.has("cardapio") || !objetoRaiz.get("cardapio").isJsonArray()) {
                throw new ArquivoImportacaoException("Estrutura inválida. Esperado campo 'cardapio' como array.");
            }

            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");

            if (listaItensJson.size() == 0) {
                throw new ArquivoImportacaoException("Nenhum item encontrado no cardápio.");
            }

            List<ItemCardapio> novosItens = new ArrayList<>();
            List<String> erros = new ArrayList<>();

            for (JsonElement elemento : listaItensJson) {
                try {
                    if (!elemento.isJsonObject()) {
                        erros.add("Item não é um objeto JSON válido.");
                        continue;
                    }

                    JsonObject itemJson = elemento.getAsJsonObject();

                    if (!itemJson.has("nome") || !itemJson.has("descricao") ||
                            !itemJson.has("preco") || !itemJson.has("categoria")) {
                        erros.add("Item incompleto: faltam campos obrigatórios.");
                        continue;
                    }

                    String nome = itemJson.get("nome").getAsString();
                    String descricao = itemJson.get("descricao").getAsString();
                    double preco = itemJson.get("preco").getAsDouble();
                    String categoriaNome = itemJson.get("categoria").getAsString();
                    boolean disponivel = itemJson.has("disponivel") ?
                            itemJson.get("disponivel").getAsBoolean() : true;

                    if (preco <= 0) {
                        erros.add("Item '" + nome + "' possui preço inválido: " + preco);
                        continue;
                    }

                    if (nome == null || nome.trim().isEmpty()) {
                        erros.add("Item com nome vazio ou nulo.");
                        continue;
                    }

                    CategoriaItem categoria = buscarOuCriarCategoria(categoriaNome);

                    if (categoria == null) {
                        erros.add("Não foi possível processar categoria para item '" + nome + "': " + categoriaNome);
                        continue;
                    }

                    try {
                        int novoId = itemRepo.gerarNovoId();
                        ItemCardapio item = new ItemCardapio(novoId, nome, descricao, preco, categoria);
                        item.setDisponivel(disponivel);

                        boolean existe = itemRepo.listarTodos().stream()
                                .anyMatch(i -> i.getNome() != null && i.getNome().equalsIgnoreCase(nome));

                        if (existe) {
                            erros.add("Item '" + nome + "' já existe no cardápio.");
                            continue;
                        }

                        novosItens.add(item);

                    } catch (PrecoInvalidoException e) {
                        erros.add("Item '" + nome + "' tem preço inválido: " + e.getMessage());
                    }

                } catch (Exception e) {
                    erros.add("Erro ao processar item: " + e.getMessage());
                }
            }

            if (!erros.isEmpty()) {
                String mensagemErro = String.join("\n", erros);
                throw new ArquivoImportacaoException("Erros encontrados durante a importação:\n" + mensagemErro);
            }

            for (ItemCardapio item : novosItens) {
                itemRepo.salvar(item);
            }

            System.out.println("✅ Importação concluída! " + novosItens.size() + " itens importados.");

        } catch (FileNotFoundException e) {
            throw new ArquivoImportacaoException("Arquivo não encontrado: " + e.getMessage());
        } catch (IOException e) {
            throw new ArquivoImportacaoException("Erro de leitura do arquivo: " + e.getMessage());
        } catch (Exception e) {
            throw new ArquivoImportacaoException("Erro inesperado: " + e.getMessage());
        }
    }

    private CategoriaItem buscarOuCriarCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            return null;
        }

        CategoriaItem categoriaExistente = categoriaRepo.buscarPorNome(nomeCategoria.trim());

        if (categoriaExistente != null) {
            return categoriaExistente;
        }

        try {
            int novoId = categoriaRepo.gerarNovoId();
            CategoriaItem novaCategoria = new CategoriaItem(
                    novoId,
                    nomeCategoria.trim(),
                    "Categoria: " + nomeCategoria
            );
            categoriaRepo.salvar(novaCategoria);
            System.out.println("📁 Nova categoria criada: " + nomeCategoria);
            return novaCategoria;
        } catch (Exception e) {
            System.err.println("Erro ao criar categoria: " + e.getMessage());
            return null;
        }
    }

    public void importarDoJson(String jsonContent) throws ArquivoImportacaoException {
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new ArquivoImportacaoException("Conteúdo JSON não pode ser vazio.");
        }

        try {
            File tempFile = File.createTempFile("importacao", ".json");
            tempFile.deleteOnExit();

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(jsonContent);
                writer.flush();
            }

            importarDoArquivo(tempFile);

        } catch (IOException e) {
            throw new ArquivoImportacaoException("Erro ao criar arquivo temporário: " + e.getMessage());
        }
    }

    public boolean validarArquivo(File arquivoJson) {
        if (arquivoJson == null || !arquivoJson.exists() || arquivoJson.length() == 0) {
            return false;
        }

        try (Reader reader = new FileReader(arquivoJson)) {
            JsonObject objetoRaiz = gson.fromJson(reader, JsonObject.class);

            if (objetoRaiz == null) {
                return false;
            }

            if (!objetoRaiz.has("cardapio") || !objetoRaiz.get("cardapio").isJsonArray()) {
                return false;
            }

            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");

            for (JsonElement elemento : listaItensJson) {
                if (!elemento.isJsonObject()) {
                    return false;
                }

                JsonObject itemJson = elemento.getAsJsonObject();

                if (!itemJson.has("nome") || !itemJson.has("descricao") ||
                        !itemJson.has("preco") || !itemJson.has("categoria")) {
                    return false;
                }

                double preco = itemJson.get("preco").getAsDouble();
                if (preco <= 0) {
                    return false;
                }
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public int contarItensNoArquivo(File arquivoJson) {
        if (!validarArquivo(arquivoJson)) {
            return 0;
        }

        try (Reader reader = new FileReader(arquivoJson)) {
            JsonObject objetoRaiz = gson.fromJson(reader, JsonObject.class);
            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");
            return listaItensJson.size();
        } catch (IOException e) {
            return 0;
        }
    }

    public List<String> listarNomesItensNoArquivo(File arquivoJson) {
        List<String> nomes = new ArrayList<>();

        if (!validarArquivo(arquivoJson)) {
            return nomes;
        }

        try (Reader reader = new FileReader(arquivoJson)) {
            JsonObject objetoRaiz = gson.fromJson(reader, JsonObject.class);
            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");

            for (JsonElement elemento : listaItensJson) {
                JsonObject itemJson = elemento.getAsJsonObject();
                String nome = itemJson.get("nome").getAsString();
                nomes.add(nome);
            }
        } catch (IOException e) {
            // Ignorar
        }

        return nomes;
    }

    public void importarComProgresso(File arquivoJson) throws ArquivoImportacaoException {
        if (!validarArquivo(arquivoJson)) {
            throw new ArquivoImportacaoException("Arquivo inválido para importação.");
        }

        int totalItens = contarItensNoArquivo(arquivoJson);
        System.out.println("📊 Total de itens a importar: " + totalItens);

        importarDoArquivo(arquivoJson);
    }
}
