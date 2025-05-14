package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ServiceRecord;
import models.SparePart;
import services.DataService;
import services.ExportService;
import services.StatisticsService;
import util.DateUtil;
import util.FileUtil;
import views.components.CustomAlert;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class ManagerController {
    private static final Logger logger = Logger.getLogger(ManagerController.class.getName());

    @FXML private TableView<ServiceRecord> recordsTable;
    @FXML private TableColumn<ServiceRecord, LocalDate> dateColumn;
    @FXML private TableColumn<ServiceRecord, String> clientColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceColumn;
    @FXML private TableColumn<ServiceRecord, String> carColumn;
    @FXML private TableColumn<ServiceRecord, Number> costColumn;
    @FXML private TableColumn<ServiceRecord, String> statusColumn;

    @FXML private TableView<SparePart> partsTable;
    @FXML private TableColumn<SparePart, String> partNameColumn;
    @FXML private TableColumn<SparePart, String> partCodeColumn;
    @FXML private TableColumn<SparePart, Number> partQuantityColumn;
    @FXML private TableColumn<SparePart, Number> partPriceColumn;

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button filterButton;
    @FXML private Button exportButton;
    @FXML private Button statsButton;
    @FXML private TextArea statsTextArea;

    private final DataService dataService = new DataService();
    private final StatisticsService statsService = new StatisticsService();
    private final ExportService exportService = new ExportService();

    @FXML
    public void initialize() {
        setupRecordsTable();
        setupPartsTable();
        setupFilters();
        loadData();
    }

    private void setupRecordsTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        carColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Форматирование даты в таблице
        dateColumn.setCellFactory(column -> new TableCell<ServiceRecord, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : DateUtil.formatDate(date));
            }
        });
    }

    private void setupPartsTable() {
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        partQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void setupFilters() {
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "Все", "Новая", "В работе", "Готово", "Отменена");
        statusFilterCombo.setItems(statusOptions);
        statusFilterCombo.getSelectionModel().selectFirst();

        LocalDate today = LocalDate.now();
        fromDatePicker.setValue(today.withDayOfMonth(1));
        toDatePicker.setValue(today);
    }

    private void loadData() {
        try {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();
            String statusFilter = statusFilterCombo.getValue();

            List<ServiceRecord> records = "Все".equals(statusFilter)
                    ? dataService.getServiceRecordsByDateRange(fromDate, toDate)
                    : dataService.getServiceRecordsByStatus(statusFilter).stream()
                    .filter(r -> !r.getDate().isBefore(fromDate) && !r.getDate().isAfter(toDate))
                    .collect(Collectors.toList());

            recordsTable.setItems(FXCollections.observableArrayList(records));
            partsTable.setItems(FXCollections.observableArrayList(dataService.getAllSpareParts()));
        } catch (Exception e) {
            logger.severe("Ошибка загрузки данных: " + e.getMessage());
            showErrorAlert("Ошибка загрузки данных", e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        loadData();
    }

    @FXML
    private void handleExport() {
        try {
            String filePath = "reports/service_report_" + System.currentTimeMillis() + ".xlsx";
            exportService.exportServiceRecordsToExcel(recordsTable.getItems(), filePath);
            showInfoAlert("Экспорт завершен", "Файл сохранен: " + filePath);
        } catch (IOException e) {
            logger.severe("Ошибка экспорта: " + e.getMessage());
            showErrorAlert("Ошибка экспорта", e.getMessage());
        }
    }

    @FXML
    private void handleShowStatistics() {
        try {
            StringBuilder stats = new StringBuilder();
            List<ServiceRecord> records = recordsTable.getItems();
            List<SparePart> parts = partsTable.getItems();

            // Заголовок с датами
            stats.append(String.format("=== Статистика за период %s - %s ===%n%n",
                    DateUtil.formatDate(fromDatePicker.getValue()),
                    DateUtil.formatDate(toDatePicker.getValue())));

            // Статистика по услугам
            appendServiceStatistics(stats, records);

            // Статистика по запчастям
            appendPartsStatistics(stats, parts);

            statsTextArea.setText(stats.toString());
        } catch (Exception e) {
            logger.severe("Ошибка генерации статистики: " + e.getMessage());
            showErrorAlert("Ошибка статистики", e.getMessage());
        }
    }

    private void appendServiceStatistics(StringBuilder stats, List<ServiceRecord> records) {
        stats.append("Услуги:\n")
                .append("- Всего записей: ").append(records.size()).append("\n")
                .append("- Общая выручка: ").append(String.format("%.2f руб.%n",
                        statsService.calculateTotalRevenue(records)))
                .append("- Средняя стоимость: ").append(String.format("%.2f руб.%n%n",
                        statsService.calculateAverageServiceCost(records)));

        stats.append("Распределение по типам услуг:\n");
        statsService.countServicesByType(records).forEach((type, count) ->
                stats.append("- ").append(type).append(": ").append(count).append("\n"));
        stats.append("\n");
    }

    private void appendPartsStatistics(StringBuilder stats, List<SparePart> parts) {
        stats.append("Запчасти:\n")
                .append("- Всего позиций: ").append(parts.size()).append("\n");

        List<SparePart> lowStock = statsService.findLowStockParts(parts);
        if (!lowStock.isEmpty()) {
            stats.append("- Недостаток запаса (").append(lowStock.size()).append("):\n");
            lowStock.forEach(part ->
                    stats.append("  - ").append(part.getName())
                            .append(" (остаток: ").append(part.getQuantity()).append(")\n"));
        }
    }

    @FXML
    private void handleCreateBackup() {
        try {
            FileUtil.createBackup(
                    dataService.getAllServiceRecords(),
                    dataService.getAllSpareParts(),
                    dataService.getAllWorkSchedules(),
                    "backups"
            );
            showInfoAlert("Резервная копия", "Данные успешно сохранены в папку backups");
        } catch (IOException e) {
            logger.severe("Ошибка резервного копирования: " + e.getMessage());
            showErrorAlert("Ошибка резервного копирования", e.getMessage());
        }
    }

    private void showInfoAlert(String title, String message) {
        new CustomAlert(Alert.AlertType.INFORMATION, title, "", message).show();
    }

    private void showErrorAlert(String title, String message) {
        new CustomAlert(Alert.AlertType.ERROR, title, "", message).show();
    }
}