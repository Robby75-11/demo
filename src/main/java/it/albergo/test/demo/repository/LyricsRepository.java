package it.albergo.test.demo.repository;

import it.albergo.test.demo.model.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LyricsRepository extends JpaRepository<Lyrics, Long> {
    // Spring crea automaticamente i metodi per il tuo database
}