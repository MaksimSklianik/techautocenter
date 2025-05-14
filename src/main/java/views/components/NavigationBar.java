package views.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.User;

public class NavigationBar extends HBox {
    private final User currentUser;
    private Runnable onLogoutHandler;

    public NavigationBar(User currentUser) {
        this.currentUser = currentUser;
        initialize();
    }

    private void initialize() {
        setSpacing(10);
        setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0;");

        // Информация о пользователе
        Label userInfo = new Label(String.format(
                "%s (%s)", currentUser.getFullName(), currentUser.getRole().name()));
        userInfo.setStyle("-fx-font-weight: bold;");

        // Кнопка выхода
        Button logoutButton = new Button("Выход");
        logoutButton.setOnAction(e -> {
            if (onLogoutHandler != null) {
                onLogoutHandler.run();
            }
        });

        getChildren().addAll(userInfo, logoutButton);
    }

    public void setOnLogout(Runnable handler) {
        this.onLogoutHandler = handler;
    }
}