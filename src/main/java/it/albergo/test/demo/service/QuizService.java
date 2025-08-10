package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.DomandaDto;
import it.albergo.test.demo.dto.QuizDto;
import it.albergo.test.demo.model.Song;
import it.albergo.test.demo.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final SongRepository songRepository;
    private final Random rnd = new Random();

    // 🔹 “Chi canta questo brano?”
    public QuizDto generaQuizBrani(int numeroDomande) {
        List<Song> tutteLeCanzoni = songRepository.findAll();
        Collections.shuffle(tutteLeCanzoni, rnd);

        List<DomandaDto> domande = new ArrayList<>();
        for (Song canzone : tutteLeCanzoni) {
            if (domande.size() >= numeroDomande) break;
            if (canzone.getArtista() == null || canzone.getArtista().getNome().isBlank()) continue;

            String rispostaCorretta = canzone.getArtista().getNome();
            Set<String> opzioni = new LinkedHashSet<>();
            opzioni.add(rispostaCorretta);

            // Genera artisti errati
            for (Song s : tutteLeCanzoni) {
                if (opzioni.size() >= 4) break;
                if (s.getArtista() != null && !s.getArtista().getNome().equalsIgnoreCase(rispostaCorretta)) {
                    opzioni.add(s.getArtista().getNome());
                }
            }

            List<String> opzioniShuff = new ArrayList<>(opzioni);
            Collections.shuffle(opzioniShuff, rnd);

            domande.add(new DomandaDto(
                    "Chi canta questo brano?",
                    opzioniShuff,
                    rispostaCorretta,
                    canzone.getId(),
                    null
            ));
        }

        return new QuizDto("Quiz: Chi canta questo brano?", "Musica", domande);
    }

    // 🔹 “Quale brano contiene questo testo?”
    public QuizDto generaQuizTesti(int numeroDomande) {
        List<Song> tutteLeCanzoni = songRepository.findAll()
                .stream()
                .filter(s -> s.getTesto() != null && !s.getTesto().isBlank()
                        && s.getTesto().trim().split("\\s+").length >= 8)
                .toList();

        Collections.shuffle(tutteLeCanzoni, rnd);
        List<DomandaDto> domande = new ArrayList<>();

        for (Song canzone : tutteLeCanzoni) {
            if (domande.size() >= numeroDomande) break;

            String snippet = getFrammento(canzone.getTesto());
            Set<String> opzioni = new LinkedHashSet<>();
            opzioni.add(canzone.getTitolo());

            for (Song s : tutteLeCanzoni) {
                if (opzioni.size() >= 4) break;
                if (!s.getTitolo().equalsIgnoreCase(canzone.getTitolo())) opzioni.add(s.getTitolo());
            }

            List<String> opzioniShuff = new ArrayList<>(opzioni);
            Collections.shuffle(opzioniShuff, rnd);

            domande.add(new DomandaDto(
                    "Quale brano contiene questo testo?",
                    opzioniShuff,
                    canzone.getTitolo(),
                    canzone.getId(),
                    snippet
            ));
        }

        return new QuizDto("Quiz: Quale brano contiene questo testo?", "Musica", domande);
    }

    // 🔹 “Quiz da un brano” (testo / artista / audio)
    public QuizDto generaQuizDaBrano(Long id, int numeroDomande) {
        Song song = songRepository.findById(id)
                .orElseGet(() -> songRepository.findByDeezerId(id).orElse(null));
        if (song == null) throw new RuntimeException("Brano non trovato");

        List<Song> tutte = songRepository.findAll();
        List<DomandaDto> domande = new ArrayList<>();

        boolean hasLyrics = song.getTesto() != null && !song.getTesto().isBlank()
                && song.getTesto().trim().split("\\s+").length >= 8;
        boolean hasAudio = (song.getPreview() != null && !song.getPreview().isBlank())
                || (song.getAudioUrl() != null && !song.getAudioUrl().isBlank());
        boolean hasArtist = song.getArtista() != null && !song.getArtista().getNome().isBlank();
        boolean hasTitle = song.getTitolo() != null && !song.getTitolo().isBlank();

        List<Integer> tipi = new ArrayList<>();
        if (hasLyrics && hasTitle) tipi.add(0); // TESTO
        if (hasArtist && hasTitle) tipi.add(1); // ARTISTA
        if (hasAudio && hasTitle) tipi.add(2);  // AUDIO

        if (tipi.isEmpty()) return new QuizDto("Quiz musicale", "Musica", domande);

        for (int i = 0; i < numeroDomande; i++) {
            int tipo = tipi.get(rnd.nextInt(tipi.size()));
            Set<String> opzioni = new LinkedHashSet<>();
            DomandaDto d;

            if (tipo == 0) { // TESTO
                String snippet = getFrammento(song.getTesto());
                opzioni.add(song.getTitolo());
                tutte.stream().map(Song::getTitolo)
                        .filter(t -> t != null && !t.equalsIgnoreCase(song.getTitolo()))
                        .limit(10).forEach(opzioni::add);

                List<String> ops = new ArrayList<>(opzioni);
                Collections.shuffle(ops, rnd);

                d = new DomandaDto("Quale brano contiene questo testo?", ops, song.getTitolo(), song.getId(), snippet);

            } else if (tipo == 1) { // ARTISTA
                String artista = song.getArtista().getNome();
                opzioni.add(artista);
                tutte.stream().map(s -> s.getArtista() != null ? s.getArtista().getNome() : null)
                        .filter(a -> a != null && !a.equalsIgnoreCase(artista))
                        .limit(10).forEach(opzioni::add);

                List<String> ops = new ArrayList<>(opzioni);
                Collections.shuffle(ops, rnd);

                d = new DomandaDto("Chi canta questo brano?", ops, artista, song.getId(), null);

            } else { // AUDIO
                String preview = song.getPreview();
                if (preview == null || preview.isBlank()) preview = song.getAudioUrl();
                if (preview == null || preview.isBlank()) continue;

                opzioni.add(song.getTitolo());
                tutte.stream().map(Song::getTitolo)
                        .filter(t -> t != null && !t.equalsIgnoreCase(song.getTitolo()))
                        .limit(10).forEach(opzioni::add);

                List<String> ops = new ArrayList<>(opzioni);
                Collections.shuffle(ops, rnd);

                d = new DomandaDto("Quale brano stai ascoltando?", ops, song.getTitolo(), song.getId(), preview);
            }

            domande.add(d);
        }

        return new QuizDto("Quiz musicale", "Musica", domande);
    }

    // Estrattore di snippet casuale
    private String getFrammento(String testo) {
        if (testo == null || testo.isBlank()) return "(Testo mancante)";
        String[] parole = testo.trim().split("\\s+");
        if (parole.length <= 7) return testo.trim();
        int start = rnd.nextInt(parole.length - 7);
        int end = Math.min(parole.length, start + 7);
        return String.join(" ", Arrays.copyOfRange(parole, start, end));
    }
}
