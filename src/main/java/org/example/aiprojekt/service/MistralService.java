package org.example.aiprojekt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.aiprojekt.model.ChatRequest;
import org.example.aiprojekt.model.ChatResponse;
import org.example.aiprojekt.model.Message;
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

    private final ObjectMapper mapper = new ObjectMapper();

    public MistralService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.
                baseUrl("https://api.mistral.ai/v1/chat/completions")
                .build();
    }

    // Generisk tekst respons
    public String askMistral(String prompt) {
        ChatRequest requestDTO = buildRequest(prompt, 350, 0.7); // default maxTokens for chat
        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(openapikey))
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        if (response == null || response.getChoices().isEmpty()) return "";
        return response.getChoices().get(0).getMessage().getContent();
    }

    // json response for quizz
    public List<Map<String, Object>> askMistralJson(String prompt) {
        for (int attempt = 0; attempt < 3; attempt++) {
            String aiText = askMistral(prompt);
            aiText = cleanJsonString(aiText); // <-- clean code fences

            try {
                JsonNode node = mapper.readTree(aiText);
                if (node.isArray()) {
                    return mapper.convertValue(node, new TypeReference<List<Map<String, Object>>>() {});
                }
            } catch (Exception e) {
                System.err.println("Invalid AI JSON on attempt " + (attempt+1) + ": " + aiText);
            }
        }

        return new ArrayList<>();
    }

    // Hjælper metode i askMistralJson
    private String cleanJsonString(String aiText) {
        if (aiText == null) return "[]";

        // Remove ```json and ``` code fences if present
        aiText = aiText.trim();
        if (aiText.startsWith("```")) {
            int firstNewline = aiText.indexOf('\n');
            if (firstNewline != -1) {
                aiText = aiText.substring(firstNewline); // remove first line ```json
            }
            if (aiText.endsWith("```")) {
                aiText = aiText.substring(0, aiText.length() - 3); // remove trailing ```
            }
        }

        return aiText.trim();
    }


    private ChatRequest buildRequest(String prompt, int maxTokens, double temperature) {
        ChatRequest requestDTO = new ChatRequest();
        requestDTO.setModel("mistral-small-latest");
        requestDTO.setTemperature(temperature);
        requestDTO.setMaxTokens(maxTokens);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system",
                "You are a helpful music assistant. " +
                        "Always provide complete answers. " +
                        "Do not truncate or cut off your response. " +
                        "If asked for JSON, return ONLY valid JSON arrays without extra text."
        ));
        messages.add(new Message("user", prompt));
        requestDTO.setMessages(messages);

        return requestDTO;
    }


    // Overloaded version for custom maxTokens
    private String askMistral(String prompt, int maxTokens, double temperature) {
        ChatRequest requestDTO = buildRequest(prompt, maxTokens, temperature);
        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(openapikey))
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        if (response == null || response.getChoices().isEmpty()) return "";
        return response.getChoices().get(0).getMessage().getContent();
    }
}
