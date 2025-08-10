package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.SongRequestDto;
import it.albergo.test.demo.dto.SongResponseDto;
import it.albergo.test.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;


    // 🔹 Crea nuova canzone
    @PostMapping
    public ResponseEntity<SongResponseDto> createSong(@RequestBody SongRequestDto request) {
        return ResponseEntity.ok(songService.createSong(request));
    }

    // 🔹 Ottieni tutte le canzoni
    @GetMapping
    public ResponseEntity<List<SongResponseDto>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // 🔹 Ottieni canzone per ID
    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDto> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    // 🔹 Cerca canzoni per titolo o artista
    @GetMapping("/search")
    public ResponseEntity<List<SongResponseDto>> searchSongs(@RequestParam String query) {
        return ResponseEntity.ok(songService.search(query));
    }
}