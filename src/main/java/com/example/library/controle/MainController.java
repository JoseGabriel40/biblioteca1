package com.example.library.controle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;

public class MainController {

    @FXML
    public void openAddBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/add_book.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Adicionar Livro");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir tela de livro: " + e.getMessage());
        }
    }

    @FXML
    public void openAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/add_user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Adicionar Usuário");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir tela de usuário: " + e.getMessage());
        }
    }

    @FXML
    private void openLoanManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/loan_management.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestão de Empréstimos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir tela de empréstimos: " + e.getMessage());
        }
    }

    @FXML
    private void openReturnedLoans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/returned_loans.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Histórico de Empréstimos Devolvidos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir histórico de devoluções: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void openUserList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/list_users.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Usuários Cadastrados");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openTopBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/top_books.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Top Livros");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao abrir top livros.");
        }
    }
}
