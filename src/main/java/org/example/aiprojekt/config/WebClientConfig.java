package org.example.aiprojekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    // Laver WebClient, så services kan injecte og builde clients med forskellige urls
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
