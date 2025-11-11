package org.example.aiprojekt.controller;

import org.example.aiprojekt.service.MistralService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chat")
public class MistralController {

    private final MistralService mistralService;


    public MistralController(MistralService mistralService) {
        this.mistralService = mistralService;
    }

    @GetMapping()
    public String chat(@RequestParam String message) {
        return mistralService.askMistral(message);
    }
}