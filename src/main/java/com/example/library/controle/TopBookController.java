package com.example.library.controle;

import com.example.library.dao.LoanDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

public class TopBookController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private void initialize() {
        try {
            // ✅ Adiciona o CSS com estilos das barras
            URL cssUrl = getClass().getResource("/View/chart-style.css");
            if (cssUrl != null) {
                barChart.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("❌ CSS não encontrado!");
            }

            LoanDAO loanDAO = new LoanDAO();
            Map<String, Integer> topBooks = loanDAO.countLoansByBook();

            int index = 0;
            for (Map.Entry<String, Integer> entry : topBooks.entrySet()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(entry.getKey());

                XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                series.getData().add(data);

                int finalIndex = index;
                barChart.getData().add(series);

                Platform.runLater(() -> {
                    if (data.getNode() != null) {
                        // ✅ Aplica cor personalizada por índice
                        data.getNode().getStyleClass().add("bar" + finalIndex);

                        // ✅ Adiciona rótulo acima da barra
                        javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf(entry.getValue()));
                        label.getStyleClass().add("bar-label");

                        label.setTranslateY(-20); // Sobe o label acima da barra
                        ((StackPane) data.getNode()).getChildren().add(label);
                    }
                });

                index++;
                if (index >= 15) break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
