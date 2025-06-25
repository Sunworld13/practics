package org.example.demo.service;

import org.example.demo.model.RequestData;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String storageDir = "storage/";
    private final String excelDir = "storage/excel/";

    public String saveToJson(RequestData data) {
        try {
            Files.createDirectories(Paths.get(storageDir));
            String fileName = storageDir + "request_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                    ".json";
            objectMapper.writeValue(new File(fileName), data);
            return "Данные успешно сохранены в JSON: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при сохранении JSON: " + e.getMessage();
        }
    }

    public String saveToExcel(RequestData data) {
        try {
            Files.createDirectories(Paths.get(excelDir));
            String fileName = excelDir + "request_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                    ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Request Data");

            // Стиль для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создаем строку с заголовками
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Email", "Message"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Заполняем данные
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(data.getName());
            dataRow.createCell(1).setCellValue(data.getEmail());
            dataRow.createCell(2).setCellValue(data.getMessage());

            // Автоподбор ширины колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Сохраняем файл
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }
            workbook.close();

            return "Данные успешно сохранены в Excel: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при сохранении Excel: " + e.getMessage();
        }
    }
}