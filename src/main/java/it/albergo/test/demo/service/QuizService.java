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

    // 🔹 “Chi canta questo brano?”
    public QuizDto generaQuizBrani(int numeroDomande) {
        List<Song> tutteLeCanzoni = songRepository.findAll();
        Collections.shuffle(tutteLeCanzoni);

        List<DomandaDto> domande = new ArrayList<>();

        for (int i = 0; i < Math.min(numeroDomande, tutteLeCanzoni.size()); i++) {
            Song canzone = tutteLeCanzoni.get(i);
            String rispostaCorretta = canzone.getArtista().getNome();

            Set<String> opzioni = new HashSet<>();
            opzioni.add(rispostaCorretta);

            // Genera 3 artisti errati
            while (opzioni.size() < 4) {
                Song casuale = tutteLeCanzoni.get(new Random().nextInt(tutteLeCanzoni.size()));
                opzioni.add(casuale.getArtista().getNome());
            }

            List<String> opzioniShuff = new ArrayList<>(opzioni);
            Collections.shuffle(opzioniShuff);

            domande.add(new DomandaDto(
                    "Chi canta il brano \"" + canzone.getTitolo() + "\"?",
                    opzioniShuff,
                    rispostaCorretta,
                    canzone.getId(),
                    null
            ));
        }

        return new QuizDto("Quiz: Chi canta questo brano?", "Musica", domande);
    }

    // 🔹 “Quale canzone contiene questo testo?”
    public QuizDto generaQuizTesti(int numeroDomande) {
        List<Song> tutteLeCanzoni = songRepository.findAll();
        Collections.shuffle(tutteLeCanzoni);

        List<DomandaDto> domande = new ArrayList<>();

        for (int i = 0; i < Math.min(numeroDomande, tutteLeCanzoni.size()); i++) {
            Song canzone = tutteLeCanzoni.get(i);
            String testo = canzone.getTesto();
            String titoloCorretto = canzone.getTitolo();

            // Prendi un frammento del testo
            String snippet = getFrammento(testo);

            Set<String> opzioni = new HashSet<>();
            opzioni.add(titoloCorretto);

            while (opzioni.size() < 4) {
                Song random = tutteLeCanzoni.get(new Random().nextInt(tutteLeCanzoni.size()));
                opzioni.add(random.getTitolo());
            }

            List<String> opzioniShuff = new ArrayList<>(opzioni);
            Collections.shuffle(opzioniShuff);

            domande.add(new DomandaDto(
                    "Quale brano contiene il testo:\n\"" + snippet + "\"",
                    opzioniShuff,
                    titoloCorretto,
                    canzone.getId(),
                    snippet
            ));
        }

        return new QuizDto("Quiz: Quale brano contiene questo testo?", "Musica", domande);
    }

    // 🔹 Genera domande da un solo brano
    public QuizDto generaQuizDaBrano(Long id, int numeroDomande) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brano non trovato"));

        String snippet = getFrammento(song.getTesto());

        List<DomandaDto> domande = new ArrayList<>();
        Set<String> opzioni = new HashSet<>();
        opzioni.add(song.getTitolo());

        List<Song> tutte = songRepository.findAll();
        while (opzioni.size() < 4) {
            Song s = tutte.get(new Random().nextInt(tutte.size()));
            opzioni.add(s.getTitolo());
        }

        List<String> opzioniShuff = new ArrayList<>(opzioni);
        Collections.shuffle(opzioniShuff);

        domande.add(new DomandaDto(
                "Quale brano contiene questo testo?\n\"" + snippet + "\"",
                opzioniShuff,
                song.getTitolo(),
                song.getId(),
                snippet
        ));

        return new QuizDto(song.getTitolo(), "Musica", domande);
    }

    private String getFrammento(String testo) {
        if (testo == null || testo.isBlank()) return "(Testo mancante)";
        String[] parole = testo.split("\\s+");
        int start = new Random().nextInt(Math.max(1, parole.length - 10));
        int end = Math.min(parole.length, start + 7);
        return String.join(" ", Arrays.copyOfRange(parole, start, end));
    }
}