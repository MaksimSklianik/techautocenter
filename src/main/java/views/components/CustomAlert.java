package views.components;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CustomAlert extends Alert {
    public CustomAlert(AlertType alertType, String title, String header, String content) {
        super(alertType);
        setTitle(title);
        setHeaderText(header);
        setContentText(content);
    }

    public CustomAlert(AlertType alertType, String title, String header, String content, Exception exception) {
        super(alertType);
        setTitle(title);
        setHeaderText(header);
        setContentText(content);

        // Создание expandable Exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Стек вызовов:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Установка expandable content в диалог
        getDialogPane().setExpandableContent(expContent);
    }

    public void display() {
        super.showAndWait();
    }
}