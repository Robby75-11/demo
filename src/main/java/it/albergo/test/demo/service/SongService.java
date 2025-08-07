package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.SongRequestDto;
import it.albergo.test.demo.dto.SongResponseDto;
import it.albergo.test.demo.model.Artist;
import it.albergo.test.demo.model.Song;
import it.albergo.test.demo.repository.ArtistRepository;
import it.albergo.test.demo.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    // 🔹 Salva nuova canzone
    public SongResponseDto createSong(SongRequestDto request) {
        Optional<Artist> artistaOpt = artistRepository.findById(request.getArtistaId());

        if (artistaOpt.isEmpty()) {
            throw new RuntimeException("Artista non trovato con ID: " + request.getArtistaId());
        }

        Song song = new Song();
        song.setTitolo(request.getTitolo());
        song.setTesto(request.getTesto());
        song.setAudioUrl(request.getAudioUrl());
        song.setArtista(artistaOpt.get());
        song.setCoverImageUrl("https://via.placeholder.com/150"); // placeholder
        song.setPreview(request.getAudioUrl()); // Se preview = audioUrl

        Song saved = songRepository.save(song);

        return convertToDto(saved);
    }

    // 🔹 Ottieni tutte le canzoni
    public List<SongResponseDto> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 Ottieni canzone per ID
    public SongResponseDto getSongById(Long id) {
        return songRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Canzone non trovata"));
    }

    // 🔹 Cerca canzoni per titolo o artista
    public List<SongResponseDto> search(String query) {
        return songRepository
                .findByTitoloContainingIgnoreCaseOrArtistaNomeContainingIgnoreCase(query, query)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 Convertitore da entity a DTO
    private SongResponseDto convertToDto(Song song) {
        SongResponseDto dto = new SongResponseDto();
        dto.setId(song.getId());
        dto.setTitolo(song.getTitolo());
        dto.setTesto(song.getTesto());
        dto.setAudioUrl(song.getAudioUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        dto.setArtista(song.getArtista().getNome());
        dto.setCreatore("admin"); // opzionale
        return dto;
    }
}
