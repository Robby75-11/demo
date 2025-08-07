package it.albergo.test.demo.repository;

import it.albergo.test.demo.model.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LyricsRepository extends JpaRepository<Lyrics, Long> {
    Optional<Lyrics> findBySong_Id(Long songId);
}