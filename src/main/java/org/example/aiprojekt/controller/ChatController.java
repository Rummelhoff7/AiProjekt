package org.example.aiprojekt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Value("${openai.api.key}")
    private String openapikey;

    private final WebClient webClient;

    public ChatController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping()
    public String chatTest(@RequestParam String message) {
        return message;
    }



    @GetMapping("/key")
    public String getKey() {
        return openapikey;
    }



    //@GetMapping("/chat")


}