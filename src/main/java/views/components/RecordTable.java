package views.components;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.ServiceRecord;

public class RecordTable extends TableView<ServiceRecord> {
    public RecordTable() {
        initialize();
    }

    private void initialize() {
        // Настройка столбцов
        TableColumn<ServiceRecord, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty().toString());

        TableColumn<ServiceRecord, String> clientCol = new TableColumn<>("Клиент");
        clientCol.setCellValueFactory(cellData -> cellData.getValue().clientNameProperty());

        TableColumn<ServiceRecord, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().clientPhoneProperty());

        TableColumn<ServiceRecord, String> serviceCol = new TableColumn<>("Услуга");
        serviceCol.setCellValueFactory(cellData -> cellData.getValue().serviceTypeProperty());

        TableColumn<ServiceRecord, String> carCol = new TableColumn<>("Модель");
        carCol.setCellValueFactory(cellData -> cellData.getValue().carModelProperty());

        TableColumn<ServiceRecord, String> plateCol = new TableColumn<>("Госномер");
        plateCol.setCellValueFactory(cellData -> cellData.getValue().clientNameProperty());

        TableColumn<ServiceRecord, Number> costCol = new TableColumn<>("Стоимость");
        costCol.setCellValueFactory(cellData -> cellData.getValue().costProperty());

        TableColumn<ServiceRecord, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Добавление столбцов
        getColumns().addAll(dateCol, clientCol, phoneCol, serviceCol, carCol, plateCol, costCol, statusCol);

        // Настройка таблицы
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}