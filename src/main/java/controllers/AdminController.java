package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import models.User;

import services.UserService;
import views.components.CustomAlert;

public class AdminController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, Boolean> activeColumn;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button toggleActiveButton;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Настройка таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Загрузка данных
        loadUsers();

        // Настройка обработчиков
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonsState());
    }

    private void loadUsers() {
        usersTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        updateButtonsState();
    }

    private void updateButtonsState() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selected != null;

        editButton.setDisable(!hasSelection);
        toggleActiveButton.setDisable(!hasSelection);

        if (hasSelection) {
            toggleActiveButton.setText(selected.isActive() ? "Деактивировать" : "Активировать");
        }
    }

    @FXML
    private void handleAddUser() {
        // Открытие диалога добавления пользователя
        User newUser = showUserDialog(null);
        if (newUser != null) {
            if (userService.addUser(newUser)) {
                loadUsers();
                new CustomAlert(Alert.AlertType.INFORMATION,
                        "Успех",
                        "Пользователь добавлен",
                        "Новый пользователь успешно создан").show();
            } else {
                new CustomAlert(Alert.AlertType.ERROR,
                        "Ошибка",
                        "Не удалось добавить пользователя",
                        "Возможно, пользователь с таким логином уже существует").show();
            }
        }
    }

    @FXML
    private void handleEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            User updatedUser = showUserDialog(selected);
            if (updatedUser != null) {
                if (userService.updateUser(updatedUser)) {
                    loadUsers();
                    new CustomAlert(Alert.AlertType.INFORMATION,
                            "Успех",
                            "Пользователь обновлен",
                            "Данные пользователя успешно изменены").show();
                } else {
                    new CustomAlert(Alert.AlertType.ERROR,
                            "Ошибка",
                            "Не удалось обновить пользователя",
                            "Проверьте введенные данные").show();
                }
            }
        }
    }

    @FXML
    private void handleToggleActive() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setActive(!selected.isActive());
            if (userService.updateUser(selected)) {
                loadUsers();
                new CustomAlert(Alert.AlertType.INFORMATION,
                        "Успех",
                        "Статус изменен",
                        "Статус пользователя успешно обновлен").show();
            } else {
                new CustomAlert(Alert.AlertType.ERROR,
                        "Ошибка",
                        "Не удалось изменить статус",
                        "Попробуйте снова").show();
            }
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadUsers();
        } else {
            usersTable.setItems(FXCollections.observableArrayList(userService.searchUsers(query)));
        }
    }

    private User showUserDialog(User user) {
        try {
            // Создание диалогового окна
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle(user == null ? "Добавление пользователя" : "Редактирование пользователя");

            // Кнопки
            ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Создание формы
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField usernameField = new TextField();
            PasswordField passwordField = new PasswordField();
            TextField fullNameField = new TextField();
            TextField phoneField = new TextField();
            ComboBox<String> roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll("ADMIN", "MANAGER", "MECHANIC");

            if (user != null) {
                usernameField.setText(user.getUsername());
                fullNameField.setText(user.getFullName());
                phoneField.setText(user.getPhone());
                roleCombo.setValue(user.getRole().name());
            } else {
                roleCombo.setValue("MECHANIC");
            }

            grid.add(new Label("Логин:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Пароль:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("ФИО:"), 0, 2);
            grid.add(fullNameField, 1, 2);
            grid.add(new Label("Телефон:"), 0, 3);
            grid.add(phoneField, 1, 3);
            grid.add(new Label("Роль:"), 0, 4);
            grid.add(roleCombo, 1, 4);

            dialog.getDialogPane().setContent(grid);

            // Преобразование результата
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    User result = user != null ? user : new User();
                    result.setUsername(usernameField.getText());
                    if (!passwordField.getText().isEmpty()) {
                        result.setPassword(passwordField.getText());
                    }
                    result.setFullName(fullNameField.getText());
                    result.setPhone(phoneField.getText());
                    result.setRole(User.Role.valueOf(roleCombo.getValue()));
                    return result;
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}