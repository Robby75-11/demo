package it.albergo.test.demo.controller;

import it.albergo.test.demo.dto.LoginRequest;
import it.albergo.test.demo.dto.RegisterDto;
import it.albergo.test.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) {
        String token = authService.register(registerDto);
        return ResponseEntity.ok(token);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Utente non autenticato");
        }

        return ResponseEntity.ok(userDetails);
    }

}

