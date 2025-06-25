package org.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestData {
    private String name;
    private String email;
    private String message;
    // Добавьте другие поля по необходимости
}
