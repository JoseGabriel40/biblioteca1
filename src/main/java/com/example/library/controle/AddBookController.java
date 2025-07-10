package com.example.library.controle;

import com.example.library.dao.BookDAO;
import com.example.library.dao.CategoryDAO;
import com.example.library.model.Book;
import com.example.library.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AddBookController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private TextField barcodeField;
    @FXML private TextField quantityField;
    @FXML private TextField searchField;


    @FXML private ComboBox<Category> categoryComboBox;

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> barcodeColumn;

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ObservableList<Book> booksData = FXCollections.observableArrayList();
    private Book livroEmEdicao = null;

    public AddBookController() throws SQLException {}

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        authorColumn.setCellValueFactory(cell -> cell.getValue().authorProperty());
        barcodeColumn.setCellValueFactory(cell -> cell.getValue().barcodeProperty());

        try {
            categoryComboBox.setItems(FXCollections.observableArrayList(categoryDAO.list()));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao carregar categorias: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        refreshTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBooks(newVal));
    }

    @FXML
    private void handleSave() {
        try {
            if (categoryComboBox.getValue() == null) {
                showAlert("Validação", "Selecione uma categoria.", Alert.AlertType.WARNING);
                return;
            }

            int ano = Integer.parseInt(yearField.getText());
            int quantidade = Integer.parseInt(quantityField.getText());

            if (livroEmEdicao == null) {
                // Novo livro
                Book book = new Book();
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setPublisher(publisherField.getText());
                book.setPublicationYear(ano);
                book.setBarcode(barcodeField.getText());
                book.setCategoryId(categoryComboBox.getValue().getId());
                book.setQuantity(quantidade);

                bookDAO.add(book);
                showAlert("Sucesso", "Livro cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Atualização
                livroEmEdicao.setTitle(titleField.getText());
                livroEmEdicao.setAuthor(authorField.getText());
                livroEmEdicao.setPublisher(publisherField.getText());
                livroEmEdicao.setPublicationYear(ano);
                livroEmEdicao.setBarcode(barcodeField.getText());
                livroEmEdicao.setCategoryId(categoryComboBox.getValue().getId());
                livroEmEdicao.setQuantity(quantidade);

                bookDAO.update(livroEmEdicao);
                showAlert("Sucesso", "Livro atualizado com sucesso!", Alert.AlertType.INFORMATION);
                livroEmEdicao = null; // resetar estado de edição
            }

            refreshTable();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Quantidade e ano devem ser números válidos.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao salvar livro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLoadToEdit() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            titleField.setText(selected.getTitle());
            authorField.setText(selected.getAuthor());
            publisherField.setText(selected.getPublisher());
            yearField.setText(String.valueOf(selected.getPublicationYear()));
            barcodeField.setText(selected.getBarcode());
            quantityField.setText(String.valueOf(selected.getQuantity()));

            for (Category cat : categoryComboBox.getItems()) {
                if (cat.getId() == selected.getCategoryId()) {
                    categoryComboBox.setValue(cat);
                    break;
                }
            }

            livroEmEdicao = selected; // agora ao salvar, será feito um UPDATE
        } else {
            showAlert("Seleção necessária", "Selecione um livro da tabela para editar.", Alert.AlertType.WARNING);
        }
    }


    @FXML
    private void handleEdit() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            titleField.setText(selected.getTitle());
            authorField.setText(selected.getAuthor());
            publisherField.setText(selected.getPublisher());
            yearField.setText(String.valueOf(selected.getPublicationYear()));
            barcodeField.setText(selected.getBarcode());
            quantityField.setText(String.valueOf(selected.getQuantity()));

            for (Category cat : categoryComboBox.getItems()) {
                if (cat.getId() == selected.getCategoryId()) {
                    categoryComboBox.setValue(cat);
                    break;
                }
            }

            livroEmEdicao = selected;
        } else {
            showAlert("Seleção necessária", "Selecione um livro da tabela para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmação");
            confirm.setHeaderText("Tem certeza que deseja excluir este livro?");
            confirm.setContentText("Título: " + selected.getTitle());

            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        bookDAO.delete(selected.getId());
                        showAlert("Sucesso", "Livro removido com sucesso!", Alert.AlertType.INFORMATION);
                        refreshTable();
                        clearFields();
                    } catch (Exception e) {
                        showAlert("Erro", "Erro ao remover livro: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("Seleção necessária", "Selecione um livro da tabela para excluir.", Alert.AlertType.WARNING);
        }
    }


    private void refreshTable() {
        try {
            booksData.setAll(bookDAO.list());
            booksTable.setItems(booksData);
        } catch (Exception e) {
            showAlert("Erro", "Erro ao carregar livros: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterBooks(String filter) {
        if (filter == null || filter.isEmpty()) {
            booksTable.setItems(booksData);
        } else {
            ObservableList<Book> filtered = FXCollections.observableArrayList();
            for (Book book : booksData) {
                if (book.getTitle().toLowerCase().contains(filter.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(filter.toLowerCase()) ||
                        book.getBarcode().toLowerCase().contains(filter.toLowerCase())) {
                    filtered.add(book);
                }
            }
            booksTable.setItems(filtered);
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        yearField.clear();
        barcodeField.clear();
        quantityField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
