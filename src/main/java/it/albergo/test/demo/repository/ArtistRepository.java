package it.albergo.test.demo.repository;

import it.albergo.test.demo.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByNome(String nome);
}
