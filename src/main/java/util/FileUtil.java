package util;

import models.ServiceRecord;
import models.SparePart;
import models.WorkSchedule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // Сохранение данных в CSV
    public static void saveRecordsToCsv(List<ServiceRecord> records, String filePath) throws IOException {
        List<String> lines = records.stream()
                .map(record -> String.join(",",
                        record.getDate().format(DATE_FORMATTER),
                        escapeCsv(record.getClientName()),
                        escapeCsv(record.getClientPhone()),
                        escapeCsv(record.getServiceType()),
                        escapeCsv(record.getCarModel()),
                        escapeCsv(record.getLicensePlate()),
                        String.valueOf(record.getCost()),
                        escapeCsv(record.getStatus()),
                        String.valueOf(record.getAssignedMechanicId()),
                        escapeCsv(record.getNotes())
                ))
                .collect(Collectors.toList());

        // Добавляем заголовки
        lines.add(0, "Дата,Клиент,Телефон,Услуга,Модель,Госномер,Стоимость,Статус,Механик,Примечания");

        Path path = Paths.get(filePath);
        Files.write(path, lines);
    }

    public static void savePartsToCsv(List<SparePart> parts, String filePath) throws IOException {
        List<String> lines = parts.stream()
                .map(part -> String.join(",",
                        escapeCsv(part.getName()),
                        escapeCsv(part.getCode()),
                        escapeCsv(part.getCompatibleModels()),
                        String.valueOf(part.getQuantity()),
                        String.valueOf(part.getPrice()),
                        escapeCsv(part.getSupplier()),
                        String.valueOf(part.getMinQuantity())
                ))
                .collect(Collectors.toList());

        lines.add(0, "Название,Код,Совместимость,Количество,Цена,Поставщик,Минимальный запас");

        Path path = Paths.get(filePath);
        Files.write(path, lines);
    }

    public static void saveScheduleToCsv(List<WorkSchedule> schedule, String filePath) throws IOException {
        List<String> lines = schedule.stream()
                .map(ws -> String.join(",",
                        String.valueOf(ws.getMechanicId()),
                        String.valueOf(ws.getRecordId()),
                        ws.getStartTime().format(DATETIME_FORMATTER),
                        ws.getEndTime().format(DATETIME_FORMATTER),
                        escapeCsv(ws.getStatus()),
                        escapeCsv(ws.getNotes())
                ))
                .collect(Collectors.toList());

        lines.add(0, "Механик,Запись,Начало,Конец,Статус,Примечания");

        Path path = Paths.get(filePath);
        Files.write(path, lines);
    }

    // Создание резервных копий
    public static void createBackup(List<ServiceRecord> records, List<SparePart> parts,
                                    List<WorkSchedule> schedule, String backupDir) throws IOException {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Path dirPath = Paths.get(backupDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        saveRecordsToCsv(records, backupDir + "/records_" + dateStr + ".csv");
        savePartsToCsv(parts, backupDir + "/parts_" + dateStr + ".csv");
        saveScheduleToCsv(schedule, backupDir + "/schedule_" + dateStr + ".csv");
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}