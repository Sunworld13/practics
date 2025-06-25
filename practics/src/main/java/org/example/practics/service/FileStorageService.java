package org.example.practics.service;

import org.example.practics.model.RequestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String storageDir = "storage/";

    public String saveToJson(Object data) {
        try {
            Files.createDirectories(Paths.get(storageDir));
            String fileName = storageDir + "request_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) +
                    ".json";
            objectMapper.writeValue(new File(fileName), data);
            return "Данные сохранены в: " + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка сохранения: " + e.getMessage();
        }
    }
}