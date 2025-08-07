package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.ArtistResponseDto;
import it.albergo.test.demo.model.Artist;
import it.albergo.test.demo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    // 🔹 Aggiungi artista
    @PostMapping
    public ResponseEntity<ArtistResponseDto> createArtist(@RequestBody Artist artist) {
        return ResponseEntity.ok(artistService.createArtist(artist));
    }

    // 🔹 Tutti gli artisti
    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    // 🔹 Cerca per nome
    @GetMapping("/search")
    public ResponseEntity<ArtistResponseDto> findByName(@RequestParam String nome) {
        return artistService.findByName(nome)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Ottieni per ID
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getById(id));
    }
}
