package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.LyricsRequest;
import it.albergo.test.demo.model.Lyrics;
import it.albergo.test.demo.model.Song;
import it.albergo.test.demo.repository.LyricsRepository;
import it.albergo.test.demo.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LyricsService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private LyricsRepository lyricsRepository;

    public List<Lyrics> getAllLyrics() {
        return lyricsRepository.findAll();
    }

    public Lyrics getLyricsById(Long id) {
        return lyricsRepository.findById(id).orElse(null);
    }

    public Lyrics getLyricsBySongId(Long songId) {
        return lyricsRepository.findBySong_Id(songId)
                .orElseThrow(() -> new RuntimeException("Lyrics non trovate per la canzone con ID: " + songId));
    }


    public Lyrics createLyrics(LyricsRequest request) {
        Song song = songRepository.findByDeezerId(request.getDeezerId())
                .orElseThrow(() -> new RuntimeException("Canzone non trovata con Deezer ID: " + request.getDeezerId()));

        Lyrics lyrics = new Lyrics();
        lyrics.setArtist(request.getArtista());
        lyrics.setTitle(request.getTitolo());
        lyrics.setLyrics("Testo non disponibile");
        lyrics.setSong(song); // ✅ Associa l'entità Song

        return lyricsRepository.save(lyrics);
    }

    public void deleteLyrics(Long id) {
        lyricsRepository.deleteById(id);
    }

    // 🔹 Metodo per chiamare API esterna e salvare il testo
    public String fetchAndSaveLyrics(LyricsRequest request) {
        String artist = request.getArtista();
        String title = request.getTitolo();

        String url = "https://api.lyrics.ovh/v1/" + artist + "/" + title;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            String testo = (String) response.get("lyrics");

            // ✅ Recupera la canzone dal Deezer ID
            Song song = songRepository.findByDeezerId(request.getDeezerId())
                    .orElseThrow(() -> new RuntimeException("Canzone non trovata per deezerId: " + request.getDeezerId()));

            Lyrics lyrics = new Lyrics();
            lyrics.setArtist(artist);
            lyrics.setTitle(title);
            lyrics.setLyrics(testo);
            lyrics.setSong(song); // ✅ Assegna la relazione con la canzone

            lyricsRepository.save(lyrics);
            return testo;
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero dei lyrics: " + e.getMessage());
        }
    }
}