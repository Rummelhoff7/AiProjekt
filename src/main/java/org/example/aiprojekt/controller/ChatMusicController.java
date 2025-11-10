package org.example.aiprojekt.controller;

import org.example.aiprojekt.service.LastFmMusicService;
import org.example.aiprojekt.service.MistralService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatmusic")
public class ChatMusicController {

    private final LastFmMusicService lastFmService;
    private final MistralService mistralService;

    public ChatMusicController(LastFmMusicService lastFmService, MistralService mistralService) {
        this.lastFmService = lastFmService;
        this.mistralService = mistralService;
    }

    @GetMapping
    public String chatAboutArtist(@RequestParam String artist) {
        Map<String, Object> info = lastFmService.getArtistInfo(artist);
        Map<String, Object> similar = lastFmService.getSimilarArtists(artist);

        Map<String, Object> artistData = (Map<String, Object>) info.get("artist");
        String bio = ((Map<String, Object>) artistData.get("bio")).get("summary").toString();
        Map<String, Object> similarArtists = (Map<String, Object>) similar.get("similarartists");

        String prompt = String.format("""
                Here is information about the artist %s: %s
                These are similar artists: %s
                Please write a short, friendly summary in English about %s,
                and explain what makes their music special.
                """,
                artist,
                bio,
                similarArtists,
                artist
        );

        return mistralService.askMistral(prompt);
    }
}
