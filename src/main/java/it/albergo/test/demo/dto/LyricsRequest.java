package it.albergo.test.demo.dto;

import lombok.Data;

@Data
public class LyricsRequest {
    private Long deezerId;
    private String titolo;
    private String artista;
}
