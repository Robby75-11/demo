package it.albergo.test.demo.dto;

import lombok.Data;

@Data
public class LyricsRequest {
    private Long deezerId;
    private String title;
    private String artist;
}
