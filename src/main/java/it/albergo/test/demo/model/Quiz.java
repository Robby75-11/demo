package it.albergo.test.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String titolo;
    private String categoria;

    @OneToMany(mappedBy = "quiz", cascade= CascadeType.ALL)
    private List<Domanda> domande;
}
