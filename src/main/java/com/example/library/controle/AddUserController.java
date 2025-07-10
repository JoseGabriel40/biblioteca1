package com.example.library.controle;

import com.example.library.dao.LibraryUserDAO;
import com.example.library.model.LibraryUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class AddUserController {

    @FXML private TextField nameField, classField, searchField;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private TableView<LibraryUser> userTable;
    @FXML private TableColumn<LibraryUser, String> nameColumn, courseColumn, classColumn;

    private final LibraryUserDAO userDAO = new LibraryUserDAO();
    private final ObservableList<LibraryUser> userList = FXCollections.observableArrayList();

    public AddUserController() throws SQLException {
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        courseColumn.setCellValueFactory(data -> data.getValue().courseProperty());
        classColumn.setCellValueFactory(data -> data.getValue().turmaProperty());

        courseComboBox.setItems(FXCollections.observableArrayList("desenvolvimento", "Redes", "zootecnia", "enfermagem"));

        loadUsers();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
    }
    private LibraryUser editingUser = null;

    private void loadUsers() {
        try {
            userList.setAll(userDAO.list());
            userTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterUsers(String filter) {
        if (filter == null || filter.isEmpty()) {
            userTable.setItems(userList);
        } else {
            ObservableList<LibraryUser> filtered = FXCollections.observableArrayList();
            for (LibraryUser user : userList) {
                if (user.getName().toLowerCase().contains(filter.toLowerCase()) ||
                        user.getCourse().toLowerCase().contains(filter.toLowerCase()) ||
                        user.getTurma().toLowerCase().contains(filter.toLowerCase())) {
                    filtered.add(user);
                }
            }
            userTable.setItems(filtered);
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (editingUser == null) {
                // Novo usuário
                LibraryUser user = new LibraryUser();
                user.setName(nameField.getText());
                user.setCourse(courseComboBox.getValue());
                user.setTurma(classField.getText());

                userDAO.add(user);
                showAlert("Sucesso", "Usuário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Edição
                editingUser.setName(nameField.getText());
                editingUser.setCourse(courseComboBox.getValue());
                editingUser.setTurma(classField.getText());

                userDAO.update(editingUser);
                showAlert("Sucesso", "Usuário atualizado com sucesso!", Alert.AlertType.INFORMATION);
                editingUser = null;
            }

            loadUsers();
            clearFields();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao salvar usuário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void clearFields() {
        nameField.clear();
        classField.clear();
        courseComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleLoadToEdit() {
        LibraryUser selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            courseComboBox.setValue(selected.getCourse());
            classField.setText(selected.getTurma());
            editingUser = selected;
        } else {
            showAlert("Atenção", "Selecione um usuário da tabela para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete() {
        LibraryUser selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmação");
            confirm.setHeaderText("Deseja excluir o usuário?");
            confirm.setContentText("Nome: " + selected.getName());

            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        userDAO.delete(selected.getId());
                        loadUsers();
                        showAlert("Sucesso", "Usuário excluído com sucesso.", Alert.AlertType.INFORMATION);
                    } catch (SQLException e) {
                        showAlert("Erro", "Erro ao excluir: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("Atenção", "Selecione um usuário para excluir.", Alert.AlertType.WARNING);
        }
    }

}
