package org.example.demo.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.example.demo.model.RequestData;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public List<RequestData> processCsvFile(MultipartFile file) throws IOException {
        List<RequestData> dataList = new ArrayList<>();

        // Указываем разделитель точка с запятой
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withCSVParser(parser)
                .build()) {

            String[] nextLine;
            boolean isFirstLine = true;

            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Пропускаем заголовок
                }

                if (nextLine.length >= 3) {
                    RequestData data = new RequestData(
                            nextLine[0].trim(),
                            nextLine[1].trim(),
                            nextLine[2].trim()
                    );

                    if (!data.getName().isEmpty() || !data.getEmail().isEmpty() || !data.getMessage().isEmpty()) {
                        dataList.add(data);
                    }
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Ошибка валидации CSV", e);
        }

        return dataList;
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
    private final String csvDir = "storage/csv/";

    public String convertCsvToJson(String csvContent) {
        try {
            Files.createDirectories(Paths.get(csvDir));
            Files.createDirectories(Paths.get(storageDir));

            // Чтение CSV
            List<RequestData> dataList = new ArrayList<>();
            try (CSVReader reader = new CSVReader(new StringReader(csvContent))) {
                List<String[]> rows = reader.readAll();

                // Пропускаем заголовок (первую строку)
                for (int i = 1; i < rows.size(); i++) {
                    String[] row = rows.get(i);
                    if (row.length >= 3) {
                        RequestData data = new RequestData(
                                row[0], // name
                                row[1], // email
                                row[2]  // message
                        );
                        dataList.add(data);
                    }
                }
            }

            // Сохранение в JSON
            String jsonFileName = storageDir + "converted_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                    ".json";

            objectMapper.writeValue(new File(jsonFileName), dataList);

            return "Данные успешно преобразованы и сохранены в: " + jsonFileName;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return "Ошибка при преобразовании CSV в JSON: " + e.getMessage();
        }
    }

    public String saveCsvData(List<RequestData> dataList) throws IOException {
        Files.createDirectories(Paths.get(storageDir));
        String fileName = storageDir + "converted_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                ".json";

        objectMapper.writeValue(new File(fileName), dataList);
        return fileName;
    }
}
