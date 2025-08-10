package it.albergo.test.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.albergo.test.demo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class JwtTool {

    @Value("${jwt.duration}")
    private long durata;

    @Value("${jwt.secret}")
    private String chiaveSegreta;

    public String createToken(User utente){
        List<String> roles = List.of("ROLE_" + utente.getRole().toString());
        Map<String, Object> claims = new HashMap<>();
        // Aggiungiamo il ruolo dell'utente come claim "role"
        // Assicurati che 'utente.getRuolo()' restituisca il ruolo come stringa (es. "AMMINISTRATORE", "UTENTE")
        // Se utente.getRuolo() restituisce un enum, usa .toString() per ottenere la stringa del ruolo
        claims.put("roles", List.of("ROLE_" + utente.getRole().toString())); // <--- MODIFICA CHIAVE QUI

        if (utente.getAvatarUrl() != null && !utente.getAvatarUrl().isEmpty()) {
            claims.put("avatarUrl", utente.getAvatarUrl());
        }
        // Creiamo il token
        return Jwts.builder()
                .claims(claims) // Aggiungi i claims al token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + durata))
                .subject(utente.getEmail())
                .signWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .compact();
    }
    // ✅ Metodo per verificare la validità del token
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Metodo per estrarre l'email (subject) dal token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ✅ NUOVO METODO: per estrarre lo username (email) dal token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // Estrae il subject (che ora è l'email)
    }

    public List<String> getRolesFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }
}
