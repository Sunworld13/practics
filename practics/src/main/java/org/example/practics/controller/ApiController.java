package org.example.practics.controller;

import org.example.practics.model.RequestData;
import org.example.practics.service.FileStorageService;
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