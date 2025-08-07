package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.QuizDto;
import it.albergo.test.demo.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    // 🔹 Quiz su brani: "Chi canta questo brano?"
    @GetMapping("/brani")
    public QuizDto generaQuizBrani(@RequestParam(defaultValue = "5") int numerodomande) {
        return quizService.generaQuizBrani(numerodomande);
    }

    // 🔹 Quiz su testi: "Quale canzone contiene questo testo?"
    @GetMapping("/testi")
    public QuizDto generaQuizTesti(@RequestParam(defaultValue = "5") int numerodomande) {
        return quizService.generaQuizTesti(numerodomande);
    }
    // 🔹 Quiz su un singolo brano selezionato
    @GetMapping("/brani/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public QuizDto generaQuizDaBrano(@PathVariable Long id,
                                     @RequestParam(defaultValue = "3") int numeroDomande) {
        return quizService.generaQuizDaBrano(id, numeroDomande);
    }
}
