package org.example.demo.service;

import org.example.demo.model.RequestData;
import org.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String storageDir = "storage/";

    public String saveToJson(RequestData data) {
        try {
            // Создаем директорию, если её нет
            Files.createDirectories(Paths.get(storageDir));

            // Генерируем имя файла с timestamp
            String fileName = storageDir + "request_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                    ".json";

            // Сериализуем объект в JSON и сохраняем в файл
            objectMapper.writeValue(new File(fileName), data);

            return "Данные успешно сохранены в файл: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при сохранении данных: " + e.getMessage();
        }
    }
}