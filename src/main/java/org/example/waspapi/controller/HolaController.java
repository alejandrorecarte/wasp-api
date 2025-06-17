package org.example.waspapi.controller;

import org.example.waspapi.service.HolaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class HolaController {

    private final HolaService holaService;

    public HolaController(HolaService holaService) {
        this.holaService = holaService;
    }

    @GetMapping("/hola")
    public CompletableFuture<String> saluda() {
        return holaService.saludarAsync();
    }
}
