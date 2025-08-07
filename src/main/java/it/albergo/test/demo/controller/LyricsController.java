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
            String lyrics = lyricsService.fetchAndSaveLyrics(request);
            return ResponseEntity.ok(lyrics);
        } catch (RuntimeException ex) {
            // Ritorna errore 404 se non trovata
            return ResponseEntity.status(404).body("Lyrics non trovate: " + ex.getMessage());
        } catch (Exception ex) {
            // Ritorna errore generico 500
            return ResponseEntity.status(500).body("Errore durante il fetch: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Lyrics getLyricsById(@PathVariable Long id) {
        return lyricsService.getLyricsById(id);
    }

    @GetMapping("/song/{songId}")
    public Lyrics getLyricsBySongId(@PathVariable Long songId) {
        try {
            return lyricsService.getLyricsBySongId(songId);
        } catch (RuntimeException e) {
            // 🔁 Se non trovate, provo a fare fetch da API esterna
            Song song = songService.getSongEntityById(songId);
            LyricsRequest request = new LyricsRequest();
            request.setArtista(song.getArtista().getNome());
            request.setTitolo(song.getTitolo());
            request.setDeezerId(song.getDeezerId());
            lyricsService.fetchAndSaveLyrics(request);

            // Riprovo dopo aver salvato
            return lyricsService.getLyricsBySongId(songId);
        }
    }
}