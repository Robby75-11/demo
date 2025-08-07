package it.albergo.test.demo.service;

import it.albergo.test.demo.dto.LoginDto;
import it.albergo.test.demo.dto.LoginRequest;
import it.albergo.test.demo.dto.RegisterDto;
import it.albergo.test.demo.enumeration.Role;
import it.albergo.test.demo.exception.NotFoundException;
import it.albergo.test.demo.exception.UnAuthorizedException;
import it.albergo.test.demo.model.User;
import it.albergo.test.demo.repository.UserRepository;
import it.albergo.test.demo.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository utenteRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterDto request) {
        if (utenteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }

        User nuovoUtente = new User();
        nuovoUtente.setUsername(request.getUsername());
        nuovoUtente.setEmail(request.getEmail());
        nuovoUtente.setPassword(passwordEncoder.encode(request.getPassword()));
        nuovoUtente.setRole(Role.USER); // oppure ADMIN se vuoi creare amministratori

        utenteRepository.save(nuovoUtente);

        return jwtTool.createToken(nuovoUtente);
    }


    public String login(LoginRequest loginrequest)  {
        User utente = utenteRepository.findByEmail(loginrequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email non trovata"));

        if (passwordEncoder.matches(loginrequest.getPassword(), utente.getPassword())) {
            return jwtTool.createToken(utente);
        } else {
            throw new UnAuthorizedException("Utente con questo email/password non trovato");
        }
    }
        }