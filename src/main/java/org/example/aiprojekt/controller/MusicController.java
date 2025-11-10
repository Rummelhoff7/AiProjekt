package org.example.aiprojekt.controller;

import org.example.aiprojekt.service.LastFmMusicService;
import org.example.aiprojekt.service.MistralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final LastFmMusicService lastFmService;

    public MusicController(LastFmMusicService lastFmService) {
        this.lastFmService = lastFmService;
    }

    @GetMapping("/artist")
    public Map<String, Object> getArtistInfo(@RequestParam String name) {
        return lastFmService.getArtistInfo(name);
    }

    @GetMapping("/similar")
    public Map<String, Object> getSimilar(@RequestParam String name) {
        return lastFmService.getSimilarArtists(name);
    }

}
