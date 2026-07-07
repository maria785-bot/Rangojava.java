package main.java.repository;

import exception.ArquivoImportacaoException;  // ← CORRIGIDO
import exception.PrecoInvalidoException;     // ← CORRIGIDO
import main.java.model.CategoriaItem;
import main.java.model.ItemCardapio;
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
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.itemRepo = new ItemCardapioRepository();
        this.categoriaRepo = new CategoriaItemRepository();
    }

    public void importarDoArquivo(File arquivoJson) throws ArquivoImportacaoException {  // ← CORRIGIDO
        if (arquivoJson == null) {
            throw new ArquivoImportacaoException("Arquivo não pode ser nulo.");  // ← CORRIGIDO
        }

        if (!arquivoJson.exists() || arquivoJson.length() == 0) {
            throw new ArquivoImportacaoException("Arquivo ausente ou vazio: " + arquivoJson.getPath());  // ← CORRIGIDO
        }

        if (!arquivoJson.getName().toLowerCase().endsWith(".json")) {
            throw new ArquivoImportacaoException("Arquivo deve ser JSON: " + arquivoJson.getName());  // ← CORRIGIDO
        }

        try (Reader reader = new FileReader(arquivoJson);
             JsonReader jsonReader = new JsonReader(reader)) {

            JsonObject objetoRaiz = gson.fromJson(jsonReader, JsonObject.class);

            if (objetoRaiz == null) {
                throw new ArquivoImportacaoException("Arquivo JSON inválido ou vazio.");  // ← CORRIGIDO
            }

            if (!objetoRaiz.has("cardapio") || !objetoRaiz.get("cardapio").isJsonArray()) {
                throw new ArquivoImportacaoException("Estrutura inválida. Esperado campo 'cardapio' como array.");  // ← CORRIGIDO
            }

            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");

            if (listaItensJson.size() == 0) {
                throw new ArquivoImportacaoException("Nenhum item encontrado no cardápio.");  // ← CORRIGIDO
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

                    // Buscar ou criar categoria
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

                    } catch (PrecoInvalidoException e) {  // ← CORRIGIDO
                        erros.add("Item '" + nome + "' tem preço inválido: " + e.getMessage());  // ← CORRIGIDO
                    }

                } catch (Exception e) {
                    erros.add("Erro ao processar item: " + e.getMessage());
                }
            }

            if (!erros.isEmpty()) {
                String mensagemErro = String.join("\n", erros);
                throw new ArquivoImportacaoException("Erros encontrados durante a importação:\n" + mensagemErro);  // ← CORRIGIDO
            }

            for (ItemCardapio item : novosItens) {
                itemRepo.salvar(item);
            }

            System.out.println("✅ Importação concluída! " + novosItens.size() + " itens importados.");

        } catch (FileNotFoundException e) {
            throw new ArquivoImportacaoException("Arquivo não encontrado: " + e.getMessage());  // ← CORRIGIDO
        } catch (IOException e) {
            throw new ArquivoImportacaoException("Erro de leitura do arquivo: " + e.getMessage());  // ← CORRIGIDO
        } catch (Exception e) {
            throw new ArquivoImportacaoException("Erro inesperado: " + e.getMessage());  // ← CORRIGIDO
        }
    }

    // Método para buscar ou criar categoria
    private CategoriaItem buscarOuCriarCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            return null;
        }

        // Buscar categoria existente pelo nome
        CategoriaItem categoriaExistente = categoriaRepo.buscarPorNome(nomeCategoria.trim());

        if (categoriaExistente != null) {
            return categoriaExistente;
        }

        // Criar nova categoria
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

    // Método adicional para importar de String (útil para testes)
    public void importarDoJson(String jsonContent) throws ArquivoImportacaoException {  // ← CORRIGIDO
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new ArquivoImportacaoException("Conteúdo JSON não pode ser vazio.");  // ← CORRIGIDO
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
            throw new ArquivoImportacaoException("Erro ao criar arquivo temporário: " + e.getMessage());  // ← CORRIGIDO
        }
    }

    // Método para validar arquivo antes de importar
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

    // Método para contar itens no arquivo
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

    // Método para obter nomes dos itens no arquivo
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

    // Método para importar com progresso (para arquivos grandes)
    public void importarComProgresso(File arquivoJson) throws ArquivoImportacaoException {  // ← CORRIGIDO
        if (!validarArquivo(arquivoJson)) {
            throw new ArquivoImportacaoException("Arquivo inválido para importação.");  // ← CORRIGIDO
        }

        int totalItens = contarItensNoArquivo(arquivoJson);
        System.out.println("📊 Total de itens a importar: " + totalItens);

        importarDoArquivo(arquivoJson);
    }
}