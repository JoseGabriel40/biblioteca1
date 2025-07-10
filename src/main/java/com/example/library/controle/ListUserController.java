package com.example.library.controle;

import com.example.library.dao.LibraryUserDAO;
import com.example.library.model.LibraryUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class ListUserController {

    @FXML private TableView<LibraryUser> userTable;
    @FXML private TableColumn<LibraryUser, Integer> idColumn;
    @FXML private TableColumn<LibraryUser, String> nameColumn;
    @FXML private TableColumn<LibraryUser, String> courseColumn;
    @FXML private TableColumn<LibraryUser, String> classColumn;

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField courseField;
    @FXML private TextField classField;

    private final LibraryUserDAO userDAO = new LibraryUserDAO();
    private final ObservableList<LibraryUser> userData = FXCollections.observableArrayList();
    private Integer editingUserId = null;

    public ListUserController() throws SQLException {}

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        courseColumn.setCellValueFactory(data -> data.getValue().courseProperty());
        classColumn.setCellValueFactory(data -> data.getValue().turmaProperty());

        loadUsers();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
    }

    private void loadUsers() {
        try {
            userData.setAll(userDAO.list());
            userTable.setItems(userData);
        } catch (SQLException e) {
            showError("Erro ao carregar usuários: " + e.getMessage());
        }
    }

    private void filterUsers(String filter) {
        if (filter == null || filter.isBlank()) {
            userTable.setItems(userData);
            return;
        }

        ObservableList<LibraryUser> filtered = FXCollections.observableArrayList();
        for (LibraryUser u : userData) {
            if (u.getName().toLowerCase().contains(filter.toLowerCase())
                    || u.getTurma().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(u);
            }
        }
        userTable.setItems(filtered);
    }

    @FXML
    private void handleLoadToEdit() {
        LibraryUser selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            courseField.setText(selected.getCourse());
            classField.setText(selected.getTurma());
            editingUserId = selected.getId();
        } else {
            showInfo("Selecione um usuário para editar.");
        }
    }


    @FXML
    private void handleDelete() {
        LibraryUser selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                userDAO.delete(selected.getId());
                loadUsers();
                showInfo("Usuário removido com sucesso.");
            } catch (SQLException e) {
                showError("Erro ao excluir: " + e.getMessage());
            }
        } else {
            showInfo("Selecione um usuário para excluir.");
        }
    }

    @FXML
    private void handleSave() {
        try {
            String name = nameField.getText();
            String course = courseField.getText();
            String turma = classField.getText();

            if (name.isBlank() || course.isBlank() || turma.isBlank()) {
                showInfo("Preencha todos os campos.");
                return;
            }

            LibraryUser user = new LibraryUser();
            user.setName(name);
            user.setCourse(course);
            user.setTurma(turma);

            if (editingUserId != null) {
                user.setId(editingUserId);
                userDAO.update(user);
                showInfo("Usuário atualizado com sucesso.");
            } else {
                userDAO.add(user);
                showInfo("Usuário cadastrado com sucesso.");
            }

            clearFields();
            loadUsers();
            editingUserId = null;

        } catch (SQLException e) {
            showError("Erro ao salvar: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        courseField.clear();
        classField.clear();
        editingUserId = null;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
