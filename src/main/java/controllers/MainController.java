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

public class MainController implements Initializable {
    @FXML private BorderPane root;
    @FXML private Label welcomeLabel;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Инициализация UI компонентов
    }

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Добро пожаловать, " + user.getFullName());
        initializeNavigation();
        loadContentForRole();
    }

    private void initializeNavigation() {
        NavigationBar navigationBar = new NavigationBar(currentUser);
        navigationBar.setOnLogout(this::handleLogout);
        root.setTop(navigationBar);
    }

    private void loadContentForRole() {
        switch (currentUser.getRole()) {
            case ADMIN -> loadAdminContent();
            case MANAGER -> loadManagerContent();
            case MECHANIC -> loadMechanicContent();
        }
    }

    private void loadAdminContent() {
        // Загрузка контента для администратора
    }

    private void loadManagerContent() {
        // Загрузка контента для менеджера
    }

    private void loadMechanicContent() {
        // Загрузка контента для механика
    }

    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            Parent root = FXMLLoader.load(Main.class.getResource("views/auth/LoginView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Техавтоцентр - Авторизация");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}