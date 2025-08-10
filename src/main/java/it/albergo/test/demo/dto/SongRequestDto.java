package it.albergo.test.demo.dto;

import lombok.Data;

@Data
public class SongRequestDto {
    private String titolo;
    private String testo;
    private String audioUrl;
    private String creatore;
    private Long artistaId;
    private Long deezerId;
}
