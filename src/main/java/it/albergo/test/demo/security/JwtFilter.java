package it.albergo.test.demo.security;

import it.albergo.test.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTool jwtTool;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtTool jwtTool, CustomUserDetailsService customUserDetailsService) {
        this.jwtTool = jwtTool;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtTool.isTokenValid(token)) {
                String email = jwtTool.getEmailFromToken(token);

                // 1) carico l'utente (per stato, password scaduta, ecc.)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 2) estraggo i ruoli dal JWT (es.: ["ROLE_USER"])
                List<String> roles = jwtTool.getRolesFromToken(token);
                List<GrantedAuthority> jwtAuthorities = roles == null ? List.of()
                        : roles.stream()
                        // Il token ha già ROLE_... -> non aggiungere prefisso
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // (opzionale ma consigliato) mergio con le authorities del DB
                List<GrantedAuthority> merged = Stream.concat(
                        userDetails.getAuthorities().stream(),
                        jwtAuthorities.stream()
                ).distinct().collect(Collectors.toList());

                // 3) imposto l'Authentication con le authorities corrette
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, merged);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}