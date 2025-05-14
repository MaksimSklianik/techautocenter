package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import config.DatabaseConfig;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Инициализация базы данных
        DatabaseConfig.initialize();

        // Загрузка главного окна аутентификации
        Parent root = FXMLLoader.load(getClass().getResource("/views/auth/LoginView.fxml"));
        Scene scene = new Scene(root, 1000, 700);

        // Настройка основного окна
        primaryStage.setTitle("Техавтоцентр - Авторизация");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Применение CSS стилей
        scene.getStylesheets().add(getClass().getResource("/resources/css/styles.css").toExternalForm());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}