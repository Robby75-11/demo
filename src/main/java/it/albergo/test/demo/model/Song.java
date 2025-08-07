package it.albergo.test.demo.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String titolo;

    @ManyToOne
    @JoinColumn(name = "artista_id")
    private  Artist artista;

    @Column(length = 1024)
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private  String testo;

    @Column(length = 1024) // Nuovo campo per l'immagine di copertina
    private String coverImageUrl;

    private  String creatore;
    private Long deezerId;  // ✅ Mancava nel tuo codice
    private String preview;
}
