package com.example.library.controle;

import com.example.library.dao.LoanDAO;
import com.example.library.model.Loan;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReturnedLoansController {

    @FXML private TableView<Loan> loanTable;
    @FXML private TableColumn<Loan, Integer> loanIdColumn;
    @FXML private TableColumn<Loan, String> userColumn;
    @FXML private TableColumn<Loan, String> bookColumn;
    @FXML private TableColumn<Loan, LocalDate> loanDateColumn;
    @FXML private TableColumn<Loan, LocalDate> returnDateColumn;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final LoanDAO loanDAO = new LoanDAO();

    @FXML
    public void initialize() {
        // Inicializa as colunas da tabela
        loanIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser().getName()));
        bookColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        loanDateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLoanDate()));
        returnDateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getReturnDate()));

        // Carrega todos os empréstimos devolvidos
        loadReturnedLoans();
    }

    private void loadReturnedLoans() {
        try {
            List<Loan> loans = loanDAO.listReturnedLoansWithDetails();
            loanTable.setItems(FXCollections.observableArrayList(loans));
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao carregar empréstimos devolvidos:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleFilterByDate() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            showAlert("Filtro Inválido", "Selecione ambas as datas de início e fim para aplicar o filtro.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Loan> filteredLoans = loanDAO.findReturnedLoansBetweenDates(start, end);
            loanTable.setItems(FXCollections.observableArrayList(filteredLoans));
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao filtrar empréstimos por data:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
