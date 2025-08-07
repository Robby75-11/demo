package it.albergo.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DomandaDto {
    private String testo;
    private List<String> opzioni;
    private String rispostaCorretta;
    private Long songId;
    private String testoCanzone;

  }
