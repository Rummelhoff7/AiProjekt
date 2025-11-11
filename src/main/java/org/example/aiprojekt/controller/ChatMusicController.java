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

    @GetMapping("/compare")
    public String compareArtists(@RequestParam String artist1, @RequestParam String artist2) {
        Map<String, Object> info1 = lastFmService.getArtistInfo(artist1);
        Map<String, Object> info2 = lastFmService.getArtistInfo(artist2);

        String bio1 = ((Map<String, Object>) ((Map<String, Object>) info1.get("artist")).get("bio")).get("summary").toString();
        String bio2 = ((Map<String, Object>) ((Map<String, Object>) info2.get("artist")).get("bio")).get("summary").toString();

        String prompt = String.format("""
        Compare these two artists and their musical styles.
        Artist 1: %s → %s
        Artist 2: %s → %s
        Write a short comparison for a music fan.
        """, artist1, bio1, artist2, bio2);

        return mistralService.askMistral(prompt);
    }


}
