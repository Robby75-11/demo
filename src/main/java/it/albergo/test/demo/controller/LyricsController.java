package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.LyricsRequest;
import it.albergo.test.demo.model.Lyrics;
import it.albergo.test.demo.model.Song;
import it.albergo.test.demo.service.LyricsService;
import it.albergo.test.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lyrics")
public class LyricsController {

    @Autowired
    private LyricsService lyricsService;

    @Autowired
    SongService songService;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchLyrics(@RequestBody LyricsRequest request) {
        try {
            // ✅ Recupera la canzone dal Deezer ID
            Song song = songService.getSongEntityByDeezerId(request.getDeezerId());
            if (song == null) {
                return ResponseEntity.status(404).body("Canzone non trovata");
            }

            // ✅ Usa Genius per scaricare e salvare il testo
            Lyrics lyrics = lyricsService.fetchAndSaveLyricsFromGenius(song);
            return ResponseEntity.ok(lyrics);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("Lyrics non trovate: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Errore durante il fetch: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Lyrics getLyricsById(@PathVariable Long id) {
        return lyricsService.getLyricsById(id);
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<?> getLyricsBySongId(@PathVariable Long songId) {
        try {
            // 1. Prova dal DB
            Lyrics lyrics = lyricsService.getLyricsBySongId(songId);
            return ResponseEntity.ok(lyrics);

        } catch (RuntimeException e) {
            try {
                // 2. Recupera la canzone
                Song song = songService.getSongEntityById(songId);

                // 3. Scarica da Genius
                lyricsService.fetchAndSaveLyricsFromGenius(song);

                // 4. Riprova
                Lyrics lyrics = lyricsService.getLyricsBySongId(songId);
                return ResponseEntity.ok(lyrics);

            } catch (Exception innerEx) {
                return ResponseEntity.status(500)
                        .body("❌ Errore durante il fetch dei lyrics: " + innerEx.getMessage());
            }
        }
    }
}