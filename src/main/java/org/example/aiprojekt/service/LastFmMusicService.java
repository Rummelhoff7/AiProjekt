package org.example.aiprojekt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class LastFmMusicService {


    private final WebClient webClient;

    @Value("${lastfm.api.key}")
    private String apiKey;


    public LastFmMusicService(WebClient.Builder webClientBuilder,
                         @Value("${lastfm.base.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Map<String, Object> getArtistInfo(String artistName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "artist.getinfo")
                        .queryParam("artist", artistName)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> getSimilarArtists(String artistName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "artist.getsimilar")
                        .queryParam("artist", artistName)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> getTopTracks(String artist) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "artist.gettoptracks")
                        .queryParam("artist", artist)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

}
