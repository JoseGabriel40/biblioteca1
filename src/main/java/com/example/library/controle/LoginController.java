package com.example.library.controle;

import com.example.library.dao.AdminUserDAO;
import com.example.library.dao.LibraryUserDAO;
import com.example.library.model.AdminUser;
import com.example.library.model.LibraryUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginController {

    @FXML private TextField emailField;         // Para admin: email | Para aluno: curso
    @FXML private PasswordField passwordField;  // Para admin: senha | Para aluno: turma
    @FXML private Label messageLabel;

    private final AdminUserDAO adminDAO;
    private final LibraryUserDAO libraryUserDAO;

    public LoginController() throws Exception {
        this.adminDAO = new AdminUserDAO();
        this.libraryUserDAO = new LibraryUserDAO();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Preencha todos os campos.");
            return;
        }

        try {
            // Primeiro tenta login como administrador
            AdminUser admin = adminDAO.getByEmailAndPassword(email, password);
            if (admin != null) {
                loadMainScreen("admin");
                return;
            }

            // Depois tenta login como aluno (curso + turma)
            LibraryUser aluno = libraryUserDAO.getByCourseAndClass(email, password);
            if (aluno != null) {
                loadMainScreen("aluno");
                return;
            }

            messageLabel.setText("Credenciais inválidas.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erro ao autenticar.");
        }
    }

    private void loadMainScreen(String tipoUsuario) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/main.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setTitle("Sistema de Biblioteca - " + tipoUsuario.toUpperCase());
        stage.setScene(new Scene(root));
        stage.show();
    }
}
