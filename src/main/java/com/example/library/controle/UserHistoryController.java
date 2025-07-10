package com.example.library.controle;

import com.example.library.dao.LoanDAO;
import com.example.library.model.Loan;
import com.example.library.model.LibraryUser;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserHistoryController {

    @FXML private TableView<Loan> historyTable;
    @FXML private TableColumn<Loan, Integer> idColumn;
    @FXML private TableColumn<Loan, String> bookColumn;
    @FXML private TableColumn<Loan, LocalDate> loanDateColumn;
    @FXML private TableColumn<Loan, LocalDate> returnDateColumn;
    @FXML private TableColumn<Loan, String> statusColumn;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final LoanDAO loanDAO = new LoanDAO();
    private LibraryUser currentUser; // salva o usuário atual para filtrar corretamente

    public void loadHistory(LibraryUser user) {
        this.currentUser = user;
        try {
            historyTable.setItems(FXCollections.observableArrayList(loanDAO.listUserHistory(user.getId())));
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao carregar histórico: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleFilterByDate() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            showAlert("Erro", "Selecione ambas as datas.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Loan> filteredLoans = loanDAO.findReturnedLoansBetweenDates(start, end);

            // Filtra apenas os empréstimos do usuario atual
            filteredLoans.removeIf(loan -> currentUser != null && loan.getUserId() != currentUser.getId());

            historyTable.setItems(FXCollections.observableArrayList(filteredLoans));
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar dados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookColumn.setCellValueFactory(cellData -> cellData.getValue().getBook().titleProperty());
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(cellData -> {
            boolean returned = cellData.getValue().isReturned();
            return new ReadOnlyStringWrapper(returned ? "Devolvido" : "Em Aberto");
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }

    // ✅ Método completo de alerta com título, mensagem e tipo
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Se quiser manter a versão anterior com 1 parâmetro
    private void showAlert(String msg) {
        showAlert("Erro", msg, Alert.AlertType.ERROR);
    }
}
