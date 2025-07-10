package com.example.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/login.fxml"));
        Scene scene = new Scene(root);

        // ✅ Adicionando o estilo
        scene.getStylesheets().add(getClass().getResource("/View/estilo.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Sistema de Biblioteca");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
