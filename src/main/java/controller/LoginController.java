package main.java.controller;

import main.java.model.Cliente;
import main.java.model.Restaurante;
import main.controller.repository.ClienteRepository;
import main.java.Repository.RestauranteRepository;
import main.java.util.HashSenha;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblErro;

    private RestauranteRepository restauranteRepo;
    private ClienteRepository clienteRepo;

    public LoginController() {
        this.restauranteRepo = new RestauranteRepository();
        this.clienteRepo = new ClienteRepository();
    }

    @FXML
    public void initialize() {
        // Verifica se é a primeira execução
        if (!restauranteRepo.existeRestauranteCadastrado()) {
            irParaTelaConfiguracao();
        }
    }

    @FXML
    void fazerLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();
        String senhaHash = HashSenha.gerarHash(senha);

        // 1. Tentar login como GERENTE
        Restaurante restaurante = restauranteRepo.carregar();
        if(restaurante != null && restaurante.getEmail().equals(email) && restaurante.getSenhaGerenteHash().equals(senhaHash)) {
            lblErro.setText("Login Gerente realizado!");
            irParaPainelGerente();
            return;
        }

        // 2. Tentar login como CLIENTE
        Cliente cliente = clienteRepo.buscarPorEmail(email);
        if(cliente != null && cliente.getSenhaHash().equals(senhaHash)) {
            lblErro.setText("Login Cliente realizado!");
            irParaTelaPrincipalCliente(cliente);
            return;
        }

        lblErro.setText("Email ou Senha incorretos!");
    }

    @FXML
    void irParaCadastroCliente(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/CadastroCliente.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irParaTelaConfiguracao() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ConfiguracaoInicial.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Configuração Inicial");
            stage.setScene(new Scene(root));
            stage.show();
            // Fechar tela de login se aberta
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irParaPainelGerente() {
        // Implementação da navegação
    }

    private void irParaTelaPrincipalCliente(Cliente cliente) {
        // Implementação da navegação
    }
}