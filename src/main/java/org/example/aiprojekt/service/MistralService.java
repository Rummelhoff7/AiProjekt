package org.example.aiprojekt.service;

import org.example.aiprojekt.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MistralService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openapikey;

    public MistralService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.mistral.ai/v1/chat/completions").build();
    }

    public String askMistral(String prompt) {

        ChatRequest requestDTO = new ChatRequest();
        requestDTO.setModel("mistral-small-latest");
        requestDTO.setTemperature(1.0);
        requestDTO.setMaxTokens(200);

        List<Message> messages = new ArrayList<>(); //en liste af messages med roller
        messages.add(new Message("system", "You are a helpful music assistant."));
        messages.add(new Message("user", prompt));
        requestDTO.setMessages(messages);


        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(openapikey))
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        return response.getChoices().get(0).getMessage().getContent();
    }
}
