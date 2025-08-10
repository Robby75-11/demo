package it.albergo.test.demo.dto;

import lombok.Data;

@Data
public class SongResponseDto {
    private Long id;
    private String titolo;
    private String artista;
    private String testo;
    private String audioUrl;
    private String coverImageUrl;
    private String creatore;
    private Long deezerId;
}

