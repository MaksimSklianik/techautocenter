package services;

import com.itextpdf.text.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import models.ServiceRecord;
import models.SparePart;
import models.WorkSchedule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void exportServiceRecordsToExcel(List<ServiceRecord> records, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Записи сервиса");

        // Стили для ячеек
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        // Создание заголовков
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Дата", "Клиент", "Телефон", "Услуга", "Модель", "Госномер", "Стоимость", "Статус", "Механик"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Заполнение данными
        int rowNum = 1;
        for (ServiceRecord record : records) {
            Row row = sheet.createRow(rowNum++);

            // Дата
            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(record.getDate().format(DATE_FORMATTER));
            dateCell.setCellStyle(dateStyle);

            // Клиент
            row.createCell(1).setCellValue(record.getClientName());

            // Телефон
            row.createCell(2).setCellValue(record.getClientPhone());

            // Услуга
            row.createCell(3).setCellValue(record.getServiceType());

            // Модель авто
            row.createCell(4).setCellValue(record.getCarModel());

            // Госномер
            row.createCell(5).setCellValue(record.getLicensePlate());

            // Стоимость
            Cell costCell = row.createCell(6);
            costCell.setCellValue(record.getCost());
            costCell.setCellStyle(currencyStyle);

            // Статус
            row.createCell(7).setCellValue(record.getStatus());

            // ID механика
            row.createCell(8).setCellValue(record.getAssignedMechanicId());
        }

        // Автонастройка ширины столбцов
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Сохранение файла
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    public void exportServiceRecordsToPdf(List<ServiceRecord> records, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Заголовок
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Отчет по услугам автосервиса", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Информация о периоде и количестве записей
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph info = new Paragraph(
                String.format("Всего записей: %d", records.size()), infoFont);
        info.setSpacingAfter(10);
        document.add(info);

        // Таблица
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Заголовки таблицы
        String[] headers = {"Дата", "Клиент", "Телефон", "Услуга", "Модель", "Госномер", "Стоимость", "Статус"};
        for (String header : headers) {
            table.addCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        // Данные
        for (ServiceRecord record : records) {
            table.addCell(record.getDate().format(DATE_FORMATTER));
            table.addCell(record.getClientName());
            table.addCell(record.getClientPhone());
            table.addCell(record.getServiceType());
            table.addCell(record.getCarModel());
            table.addCell(record.getLicensePlate());
            table.addCell(String.format("%.2f руб.", record.getCost()));
            table.addCell(record.getStatus());
        }

        document.add(table);
        document.close();
    }

    public void exportSparePartsToExcel(List<SparePart> parts, String filePath) throws IOException {
        // Реализация экспорта запчастей в Excel
    }

    public void exportWorkScheduleToPdf(List<WorkSchedule> schedule, String filePath) throws DocumentException, IOException {
        // Реализация экспорта расписания в PDF
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00\" руб.\""));
        return style;
    }
}