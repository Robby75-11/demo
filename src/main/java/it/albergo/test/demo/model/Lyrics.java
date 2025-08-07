package it.albergo.test.demo.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "lyrics")
@Data
public class Lyrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(name = "artist")
    private String artist;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics;

    @OneToOne
    @JoinColumn(name = "song_id")
    private Song song;
}

