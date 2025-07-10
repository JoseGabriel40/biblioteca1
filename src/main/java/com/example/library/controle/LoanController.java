package com.example.library.controle;

import com.example.library.dao.BookDAO;
import com.example.library.dao.LibraryUserDAO;
import com.example.library.dao.LoanDAO;
import com.example.library.model.Book;
import com.example.library.model.LibraryUser;
import com.example.library.model.Loan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanController {

    @FXML
    private TextField userSearchField;

    @FXML
    private TextField bookSearchField;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TableView<Loan> loanTable;

    @FXML
    private TableColumn<Loan, Integer> loanIdColumn;

    @FXML
    private TableColumn<Loan, String> userColumn;

    @FXML
    private TableColumn<Loan, String> bookColumn;

    @FXML
    private TableColumn<Loan, LocalDate> loanDateColumn;

    @FXML
    private TableColumn<Loan, LocalDate> returnDateColumn;

    private LibraryUserDAO userDAO;
    private BookDAO bookDAO;
    private LoanDAO loanDAO;

    private LibraryUser selectedUser;
    private Book selectedBook;

    public LoanController() {
        try {
            userDAO = new LibraryUserDAO();
            bookDAO = new BookDAO();
            loanDAO = new LoanDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao conectar com o banco de dados.", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void searchUser() {
        String userQuery = userSearchField.getText();
        try {
            Optional<LibraryUser> opt = userDAO.list().stream()
                    .filter(u -> u.getName().equalsIgnoreCase(userQuery)
                            || String.valueOf(u.getId()).equals(userQuery))
                    .findFirst();

            if (opt.isPresent()) {
                selectedUser = opt.get();
                showAlert("Usuário selecionado: " + selectedUser.getName());
            } else {
                showAlert("Usuário não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao buscar usuário.");
        }
    }

    @FXML
    private void searchBook() {
        String bookQuery = bookSearchField.getText();
        try {
            Optional<Book> opt = bookDAO.list().stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(bookQuery)
                            || String.valueOf(b.getId()).equals(bookQuery))
                    .findFirst();

            if (opt.isPresent()) {
                selectedBook = opt.get();
                showAlert("Livro selecionado: " + selectedBook.getTitle());
            } else {
                showAlert("Livro não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao buscar livro.");
        }
    }

    @FXML
    private void handleLoan() {
        if (selectedUser == null || selectedBook == null || loanDatePicker.getValue() == null || returnDatePicker.getValue() == null) {
            showAlert("Validação", "Preencha todos os campos corretamente antes de registrar o empréstimo.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Book livroAtualizado = bookDAO.getById(selectedBook.getId());

            if (livroAtualizado.getQuantity() <= 0) {
                showAlert("Indisponível", "Este livro não está disponível para empréstimo.", Alert.AlertType.WARNING);
                return;
            }

            boolean jaEmprestado = loanDAO.hasActiveLoan(selectedUser.getId(), selectedBook.getId());

            if (jaEmprestado) {
                showAlert("Aviso", "Este usuário já possui este livro emprestado.", Alert.AlertType.WARNING);
                return;
            }

            Loan loan = new Loan();
            loan.setUserId(selectedUser.getId());
            loan.setBookId(selectedBook.getId());
            loan.setLoanDate(loanDatePicker.getValue());
            loan.setReturnDate(returnDatePicker.getValue());

            loanDAO.registerLoan(loan);
            bookDAO.decrementQuantity(selectedBook.getId());
            showAlert("Sucesso", "Empréstimo registrado com sucesso!", Alert.AlertType.INFORMATION);
            clearFields();
            refreshLoanTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao registrar empréstimo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleReturn() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            showAlert("Aviso", "Selecione um empréstimo para devolução.", Alert.AlertType.WARNING);
            return;
        }

        try {
            loanDAO.registerReturn(selectedLoan.getId());
            bookDAO.incrementQuantity(selectedLoan.getBookId());
            showAlert("Sucesso", "Livro devolvido com sucesso!", Alert.AlertType.INFORMATION);
            refreshLoanTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao registrar devolução.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void initialize() {
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        bookColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        refreshLoanTable();
    }

    private void refreshLoanTable() {
        try {
            loanTable.setItems(FXCollections.observableArrayList(
                    loanDAO.listOpenLoansWithDetails()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao carregar empréstimos.", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        userSearchField.clear();
        bookSearchField.clear();
        loanDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        selectedUser = null;
        selectedBook = null;
    }

    private void showAlert(String message) {
        showAlert("Mensagem", message, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openUserHistory() {
        if (selectedUser == null) {
            showAlert("Selecione um usuário primeiro.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/user_history.fxml"));
            Parent root = loader.load();

            UserHistoryController controller = loader.getController();
            controller.loadHistory(selectedUser);

            Stage stage = new Stage();
            stage.setTitle("Histórico de Empréstimos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível abrir o histórico.", Alert.AlertType.ERROR);
        }
    }

}
