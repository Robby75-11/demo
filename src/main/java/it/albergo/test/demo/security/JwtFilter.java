package it.albergo.test.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTool jwtTool;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtTool jwtTool, UserDetailsService userDetailsService) {
        this.jwtTool = jwtTool;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // continua come utente anonimo
            return;
        }

        try {
            String token = authHeader.substring(7);
            jwtTool.validateToken(token); // verifica firma e scadenza

            String email = jwtTool.getUsernameFromToken(token);
            List<String> roles = jwtTool.getRolesFromToken(token); // ✅ Lista dei ruoli

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(
                            role.startsWith("ROLE_") ? role : "ROLE_" + role
                    ))
                    .collect(Collectors.toList());



            UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "", authorities);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            System.out.println("Errore autenticazione JWT: " + ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

            return path.startsWith("/auth/forgot-password") || path.startsWith("/auth/reset-password")
                    || path.startsWith("/auth/login") || path.startsWith("/auth/register")||
                    (path.startsWith("/api/lyrics") && request.getMethod().equals("GET"));
        }
    }




