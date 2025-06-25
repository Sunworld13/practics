package org.example.demo.controller;

import org.example.demo.model.RequestData;
import org.example.demo.service.FileStorageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final FileStorageService fileStorageService;

    public ApiController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/save-request")
    public String saveRequest(@RequestBody RequestData requestData) {
        return fileStorageService.saveToJson(requestData);
    }
}