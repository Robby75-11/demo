package it.albergo.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuizDto {
    private String titolo;
    private String categoria;
    private List<DomandaDto> domande;
}


