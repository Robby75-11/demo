package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.LyricsRequest;
import it.albergo.test.demo.model.Lyrics;
import it.albergo.test.demo.service.LyricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lyrics")
public class LyricsController {

    @Autowired
    private LyricsService lyricsService;

    @PostMapping("/fetch")
    public ResponseEntity<
            String> fetchLyrics(@RequestBody LyricsRequest request) {
        String lyrics = lyricsService.fetchAndSaveLyrics(request);
        return ResponseEntity.ok(lyrics);
    }


    @GetMapping("/{id}")
    public Lyrics getLyricsById(@PathVariable Long id) {
        return lyricsService.getLyricsById(id);
    }
}