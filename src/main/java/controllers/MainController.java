package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.User;
import org.example.Main;
import views.components.NavigationBar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML private BorderPane root;
    @FXML private Label welcomeLabel;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Дополнительная инициализация UI при необходимости
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateWelcomeMessage();
        initializeNavigation();
        loadContentForRole();
    }

    private void updateWelcomeMessage() {
        welcomeLabel.setText("Добро пожаловать, " + currentUser.getFullName());
    }

    private void initializeNavigation() {
        try {
            NavigationBar navigationBar = new NavigationBar(currentUser);
            navigationBar.setOnLogout(this::handleLogout);
            root.setTop(navigationBar);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка инициализации навигации", e);
            showErrorAlert("Ошибка", "Не удалось загрузить панель навигации");
        }
    }

    private void loadContentForRole() {
        try {
            switch (currentUser.getRole()) {
                case ADMIN -> loadAdminContent();
                case MANAGER -> loadManagerContent();
                case MECHANIC -> loadMechanicContent();
                default -> showErrorAlert("Ошибка", "Неизвестная роль пользователя");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка загрузки контента для роли " + currentUser.getRole(), e);
            showErrorAlert("Ошибка", "Не удалось загрузить интерфейс для вашей роли");
        }
    }

    private void loadAdminContent() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/admin/AdminDashboard.fxml"));
        Parent adminContent = loader.load();
        AdminController controller = loader.getController();
        controller.setUser(currentUser);
        root.setCenter(adminContent);
    }

    private void loadManagerContent() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/manager/ManagerDashboard.fxml"));
        Parent managerContent = loader.load();
        ManagerController controller = loader.getController();
        controller.setUser(currentUser);
        root.setCenter(managerContent);
    }

    private void loadMechanicContent() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/mechanic/MechanicDashboard.fxml"));
        Parent mechanicContent = loader.load();
        MechanicController controller = loader.getController();
        controller.setUser(currentUser);
        root.setCenter(mechanicContent);
    }

    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            Parent loginView = FXMLLoader.load(Main.class.getResource("views/auth/LoginView.fxml"));
            Scene scene = new Scene(loginView);
            scene.getStylesheets().add(Main.class.getResource("styles/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Техавтоцентр - Авторизация");
            stage.centerOnScreen();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при выходе из системы", e);
            showErrorAlert("Ошибка", "Не удалось выполнить выход");
        }
    }

    private void showErrorAlert(String title, String message) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}