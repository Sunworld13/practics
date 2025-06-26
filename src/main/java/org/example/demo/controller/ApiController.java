package org.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.example.demo.model.RequestData;
import org.example.demo.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final FileStorageService fileStorageService;

    public ApiController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/save-request")
    public String saveRequest(@RequestBody RequestData requestData,
                              @RequestParam(defaultValue = "json") String format) {
        if ("excel".equalsIgnoreCase(format)) {
            return fileStorageService.saveToExcel(requestData);
        }
        return fileStorageService.saveToJson(requestData);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String storageDir = "storage/";

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Создаем папку storage, если её нет
            Files.createDirectories(Paths.get(storageDir));

            // Читаем Excel файл
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                if (sheet.getPhysicalNumberOfRows() < 2) {
                    return ResponseEntity.badRequest().body("Файл должен содержать хотя бы 2 строки");
                }

                Row dataRow = sheet.getRow(1); // Вторая строка с данными

                // Создаем объект для сохранения
                RequestData data = new RequestData(
                        getCellValueAsString(dataRow.getCell(0)), // name
                        getCellValueAsString(dataRow.getCell(1)), // email
                        getCellValueAsString(dataRow.getCell(2))  // message
                );

                // Генерируем имя файла
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String fileName = storageDir + "data_" + timestamp + ".json";

                // Сохраняем в JSON
                objectMapper.writeValue(new File(fileName), data);

                return ResponseEntity.ok("Данные сохранены в файл: " + fileName);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка: " + e.getMessage());
        }
    }


    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Обработка CSV файла
            List<RequestData> dataList = fileStorageService.processCsvFile(file);

            // 2. Проверка наличия данных
//            if (dataList.isEmpty()) {
//                return ResponseEntity.badRequest().body("CSV файл не содержит данных или имеет неверный формат");
//            }

            // 3. Сохранение в JSON
            String resultPath = fileStorageService.saveCsvData(dataList);

            return ResponseEntity.ok("Данные успешно обработаны и сохранены в: " + resultPath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка обработки CSV файла: " + e.getMessage());
        }
    }
    @PostMapping("/csv-to-json")
    public ResponseEntity<String> convertCsvToJson(@RequestBody String csvContent) {
        try {
            String result = fileStorageService.convertCsvToJson(csvContent);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка преобразования: " + e.getMessage());
        }
    }
}

