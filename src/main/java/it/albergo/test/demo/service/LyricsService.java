package it.albergo.test.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.albergo.test.demo.model.Lyrics;
import it.albergo.test.demo.model.Song;
import it.albergo.test.demo.repository.LyricsRepository;
import it.albergo.test.demo.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LyricsService {

    private final LyricsRepository lyricsRepository;
    private final SongRepository songRepository;
    private final RestTemplate restTemplate = new RestTemplate();



    // --- CRUD semplici -------------------------------------------------------
    public List<Lyrics> getAllLyrics()       { return lyricsRepository.findAll(); }
    public Lyrics getLyricsById(Long id)     { return lyricsRepository.findById(id).orElse(null); }

    /** Ritorna dal DB o, se assente, fa fetch da Genius e salva */
    public Lyrics getLyricsBySongId(Long songId) {
        return lyricsRepository.findBySong_Id(songId)
                .orElseGet(() -> {
                    Song song = songRepository.findById(songId)
                            .orElseThrow(() -> new RuntimeException("Canzone non trovata"));
                    return fetchAndSaveLyricsFromGenius(song);
                });
    }

    // dipendenze usate qui:
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.jsoup.Jsoup;

    @Transactional
    public Lyrics fetchAndSaveLyricsFromGenius(Song song) {
        try {
            // --- 0) precondizioni ---
            if (song == null || song.getArtista() == null) return saveFallback(song);
            String artista = song.getArtista().getNome();
            String titolo  = song.getTitolo();
            if (isBlank(artista) || isBlank(titolo)) return saveFallback(song);

            // --- 1) /search su Genius ---
            String query = URLEncoder.encode(artista + " " + titolo, StandardCharsets.UTF_8);
            String searchUrl = "https://api.genius.com/search?q=" + query;

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (LyricsFetcher/1.0)");
            headers.set(HttpHeaders.ACCEPT, "application/json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return saveFallback(song);

            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(resp.getBody());
            JsonNode hits = root.path("response").path("hits");
            if (!hits.isArray() || hits.isEmpty()) return saveFallback(song);

            // prendiamo il primo risultato
            JsonNode first = hits.get(0).path("result");
            String lyricsUrl = first.path("url").asText(null);
            if (isBlank(lyricsUrl)) return saveFallback(song);

            // --- 2) scarica pagina lyrics con User-Agent ---
            HttpHeaders pageHeaders = new HttpHeaders();
            pageHeaders.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (LyricsFetcher/1.0)");
            HttpEntity<Void> pageEntity = new HttpEntity<>(pageHeaders);

            ResponseEntity<String> pageResp = restTemplate.exchange(lyricsUrl, HttpMethod.GET, pageEntity, String.class);
            String html = pageResp.getBody();
            if (isBlank(html)) return saveFallback(song);

            // --- 3) parse HTML: div[data-lyrics-container="true"] (più div) ---
            var doc = Jsoup.parse(html);
            var blocks = doc.select("div[data-lyrics-container='true']");
            String text;
            if (!blocks.isEmpty()) {
                text = blocks.eachText().stream().collect(Collectors.joining("\n"));
            } else {
                // fallback layout vecchio
                text = doc.select("div.lyrics").text();
            }
            if (isBlank(text)) return saveFallback(song);

            // --- 4) salva ---
            Lyrics l = new Lyrics();
            l.setSong(song);
            l.setArtist(artista);
            l.setTitle(titolo);
            l.setLyrics(text);
            return lyricsRepository.save(l);

        } catch (HttpStatusCodeException e) {
            // gestione 429/5xx basilare (opzionale: aggiungi retry con backoff)
            System.err.println("Genius HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return saveFallback(song);
        } catch (Exception e) {
            System.err.println("Genius error: " + e.getMessage());
            return saveFallback(song);
        }
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private Lyrics saveFallback(Song song) {
        Lyrics l = new Lyrics();
        l.setSong(song);
        l.setArtist(song != null && song.getArtista()!=null ? song.getArtista().getNome() : null);
        l.setTitle(song != null ? song.getTitolo() : null);
        l.setLyrics("Testo non disponibile");
        return lyricsRepository.save(l);
    }

}
