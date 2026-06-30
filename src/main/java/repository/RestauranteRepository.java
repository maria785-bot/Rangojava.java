package main.java.Repository;

import model.Restaurante;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class RestauranteRepository {
    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/restaurante.json";
    private final Gson gson;

    public RestauranteRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        criarArquivoSeNaoExistir();
    }

    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                arquivo.getParentFile().mkdirs();
                arquivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void salvar(Restaurante restaurante) {
        try (Writer writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(restaurante, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Restaurante carregar() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) return null;

        try (Reader reader = new FileReader(arquivo)) {
            return gson.fromJson(reader, Restaurante.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existeRestauranteCadastrado() {
        return carregar() != null;
    }
}