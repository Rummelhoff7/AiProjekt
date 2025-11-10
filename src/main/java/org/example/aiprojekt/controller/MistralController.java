package org.example.aiprojekt.controller;

import org.example.aiprojekt.service.MistralService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


@RestController
@RequestMapping("/api/chat")
public class MistralController {

    private final MistralService mistralService;

    private final WebClient webClient;

    public MistralController(MistralService mistralService, WebClient.Builder webClientBuilder) {
        this.mistralService = mistralService;
        this.webClient = webClientBuilder.baseUrl("https://api.mistral.ai/v1/chat/completions").build();
    }

    @GetMapping()
    public String chat(@RequestParam String message) {
        return mistralService.askMistral(message);
    }
}