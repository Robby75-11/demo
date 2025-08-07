package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.ArtistResponseDto;
import it.albergo.test.demo.model.Artist;
import it.albergo.test.demo.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    // 🔹 Aggiungi un nuovo artista
    public ArtistResponseDto createArtist(Artist artist) {
        Artist saved = artistRepository.save(artist);
        return convertToDto(saved);
    }

    // 🔹 Ottieni tutti gli artisti
    public List<ArtistResponseDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 Cerca artista per nome
    public Optional<ArtistResponseDto> findByName(String nome) {
        return artistRepository.findByNome(nome).map(this::convertToDto);
    }

    // 🔹 Ottieni artista per ID
    public ArtistResponseDto getById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista non trovato con ID: " + id));
        return convertToDto(artist);
    }

    // 🔹 Converte entity -> DTO
    private ArtistResponseDto convertToDto(Artist artist) {
        ArtistResponseDto dto = new ArtistResponseDto();
        dto.setId(artist.getId());
        dto.setNome(artist.getNome());
        dto.setImmagineUrl(artist.getImageUrl());
        return dto;
    }
}
