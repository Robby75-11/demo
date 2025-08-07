package it.albergo.test.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
@Entity
@Data
public class Domanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testo;
    @ElementCollection
    private List<String> opzioni;

    private String rispostaCorretta;

    @ManyToOne
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "lyrics_id")
    private Lyrics lyrics;
}
