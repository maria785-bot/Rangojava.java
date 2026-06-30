package main.java.Repository;

import exception.ArquivoImportacaoException;
import exception.PrecoInvalidoException;
import model.CategoriaItem;
import model.ItemCardapio;
import com.google.gson.Gson;
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

    public ImportacaoCardapioService() {
        this.gson = new Gson();
        this.itemRepo = new ItemCardapioRepository();
    }

    public void importarDoArquivo(File arquivoJson) throws ArquivoImportacaoException {
        if (!arquivoJson.exists() || arquivoJson.length() == 0) {
            throw new ArquivoImportacaoException("Arquivo ausente ou vazio.");
        }

        try (Reader reader = new FileReader(arquivoJson);
             JsonReader jsonReader = new JsonReader(reader)) {

            JsonObject objetoRaiz = gson.fromJson(jsonReader, JsonObject.class);

            if (!objetoRaiz.has("cardapio") || !objetoRaiz.get("cardapio").isJsonArray()) {
                throw new ArquivoImportacaoException("Estrutura inválida. Esperado campo 'cardapio'.");
            }

            JsonArray listaItensJson = objetoRaiz.getAsJsonArray("cardapio");
            List<ItemCardapio> novosItens = new ArrayList<>();

            for (JsonElement elemento : listaItensJson) {
                JsonObject itemJson = elemento.getAsJsonObject();

                if (!itemJson.has("nome") || !itemJson.has("descricao") || !itemJson.has("preco") || !itemJson.has("categoria")) {
                    throw new ArquivoImportacaoException("Item incompleto: faltam campos obrigatórios.");
                }

                String nome = itemJson.get("nome").getAsString();
                String descricao = itemJson.get("descricao").getAsString();
                double preco = itemJson.get("preco").getAsDouble();
                String categoriaStr = itemJson.get("categoria").getAsString();
                boolean disponivel = itemJson.has("disponivel") ? itemJson.get("disponivel").getAsBoolean() : true;
                String imagemPath = itemJson.has("imagemPath") ? itemJson.get("imagemPath").getAsString() : null;

                CategoriaItem categoria;
                try {
                    categoria = CategoriaItem.valueOf(categoriaStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ArquivoImportacaoException("Categoria desconhecida: " + categoriaStr);
                }

                try {
                    int novoId = itemRepo.gerarNovoId();
                    ItemCardapio item = new ItemCardapio(novoId, nome, descricao, preco, categoria);
                    item.setDisponivel(disponivel);
                    item.setCaminhoImagem(imagemPath);
                    novosItens.add(item);
                } catch (PrecoInvalidoException e) {
                    throw new ArquivoImportacaoException("No item '" + nome + "': " + e.getMessage());
                }
            }

            for (ItemCardapio item : novosItens) {
                itemRepo.salvar(item);
            }

        } catch (IOException e) {
            throw new ArquivoImportacaoException("Erro de leitura: " + e.getMessage());
        }
    }
}