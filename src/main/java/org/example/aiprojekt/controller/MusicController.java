package org.example.aiprojekt.controller;

import org.example.aiprojekt.service.LastFmMusicService;
import org.example.aiprojekt.service.MistralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final LastFmMusicService lastFmService;
    private final MistralService mistralService;

    public MusicController(LastFmMusicService lastFmService, MistralService mistralService) {
        this.lastFmService = lastFmService;
        this.mistralService = mistralService;
    }

    @GetMapping("/artist")
    public Map<String, Object> getArtistInfo(@RequestParam String name) {
        return lastFmService.getArtistInfo(name);
    }

    @GetMapping("/similar")
    public Map<String, Object> getSimilar(@RequestParam String name) {
        return lastFmService.getSimilarArtists(name);
    }

    @GetMapping("/top-tracks")
    public Map<String, Object> getTopTracks(@RequestParam String artist) {
        return lastFmService.getTopTracks(artist);
    }


    @GetMapping("/quiz")
    public List<Map<String, Object>> getArtistQuiz(@RequestParam String artist) {
        Map<String, Object> info = lastFmService.getArtistInfo(artist);
        Map<String, Object> artistData = (Map<String, Object>) info.get("artist");
        String bio = ((Map<String, Object>) artistData.get("bio")).get("summary").toString();


        //Laver en prompt for Mistral
        String prompt = String.format("""
                 You are a JSON-based music quiz generator.
                 Based on this information about the artist %s: %s
                 Create a quiz with 3 questions about this artist.
                                 
                 Each question must be a JSON object with:
                 {
                 "question": "...",
                 "options": ["A) ...", "B) ...", "C) ...", "D) ..."],
                 "correct": "A"
                 }
                
                 Return ONLY a JSON array. Do not include any extra text, explanations, or markdown outside the array.
                 Ensure the JSON is valid.
                 """, artist, bio);

        return mistralService.askMistralJson(prompt);
    }
}