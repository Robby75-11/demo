package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.LyricsRequest;
import it.albergo.test.demo.model.Lyrics;
import it.albergo.test.demo.repository.LyricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LyricsService {

    @Autowired
    private LyricsRepository lyricsRepository;

    public List<Lyrics> getAllLyrics() {
        return lyricsRepository.findAll();
    }

    public Lyrics getLyricsById(Long id) {
        return lyricsRepository.findById(id).orElse(null);
    }

    public Lyrics getLyricsBySongId(Long songId) {
        return lyricsRepository.findAll().stream()
                .filter(l -> l.getSongId() != null && l.getSongId().equals(songId))
                .findFirst()
                .orElse(null);
    }

    public Lyrics createLyrics(LyricsRequest request) {
        Lyrics lyrics = new Lyrics();
        lyrics.setArtist(request.getArtista());
        lyrics.setTitle(request.getTitolo());
        lyrics.setLyrics("Testo non disponibile");
        lyrics.setSongId(request.getDeezerId());
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

            Lyrics lyrics = new Lyrics();
            lyrics.setArtist(artist);
            lyrics.setTitle(title);
            lyrics.setLyrics(testo);
            lyrics.setSongId(request.getDeezerId());

            lyricsRepository.save(lyrics);
            return testo;
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero dei lyrics: " + e.getMessage());
        }
    }
}
