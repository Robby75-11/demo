package it.albergo.test.demo.repository;

import it.albergo.test.demo.model.Artist;
import it.albergo.test.demo.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtista(Artist artista);

    List<Song> findByArtistaNome(String nome);

    List<Song> findByTitoloContainingIgnoreCaseOrArtistaNomeContainingIgnoreCase(String titolo, String artistaNome);

    Optional<Song> findByTitoloAndArtista(String titolo, Artist artista);



}
