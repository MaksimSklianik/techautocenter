package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import models.User;
import org.example.Main;
import services.AuthService;
import util.PasswordUtil;
import views.components.CustomAlert;

import java.io.IOException;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private ProgressIndicator progressIndicator;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Настройка валидации полей
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());

        // Скрываем индикатор прогресса по умолчанию
        progressIndicator.setVisible(false);

        // Обработка нажатия Enter в полях ввода
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
    }

    private void validateFields() {
        boolean valid = !usernameField.getText().trim().isEmpty() &&
                !passwordField.getText().trim().isEmpty();
        loginButton.setDisable(!valid);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Показываем индикатор загрузки
        progressIndicator.setVisible(true);
        loginButton.setDisable(true);
        errorLabel.setVisible(false);

        // Запускаем аутентификацию в отдельном потоке
        new Thread(() -> {
            try {
                // Имитация задержки для демонстрации
                Thread.sleep(1000);

                User authenticatedUser = authService.authenticate(username, password).orElse(null);

                // Возвращаемся в UI поток
                javafx.application.Platform.runLater(() -> {
                    if (authenticatedUser != null) {
                        openMainView(authenticatedUser);
                    } else {
                        showError("Неверный логин или пароль");
                    }
                    progressIndicator.setVisible(false);
                    loginButton.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Ошибка при аутентификации: " + e.getMessage());
                    progressIndicator.setVisible(false);
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/auth/RegisterView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Техавтоцентр - Регистрация");
        } catch (IOException e) {
            new CustomAlert(Alert.AlertType.ERROR,
                    "Ошибка",
                    "Не удалось открыть форму регистрации",
                    e.getMessage()).show();
        }
    }

    private void openMainView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/MainView.fxml"));
            Parent root = loader.load();

            // Передаем аутентифицированного пользователя в главный контроллер
            MainController mainController = loader.getController();
            mainController.setUser(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/resources/css/styles.css").toExternalForm());
            stage.setScene(scene);

            // Настраиваем окно в зависимости от роли
            switch (user.getRole()) {
                case ADMIN -> stage.setTitle("Техавтоцентр - Администратор");
                case MANAGER -> stage.setTitle("Техавтоцентр - Менеджер");
                case MECHANIC -> stage.setTitle("Техавтоцентр - Механик");
            }

            stage.centerOnScreen();
        } catch (IOException e) {
            new CustomAlert(Alert.AlertType.ERROR,
                    "Ошибка",
                    "Не удалось загрузить главное окно",
                    e.getMessage()).show();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}