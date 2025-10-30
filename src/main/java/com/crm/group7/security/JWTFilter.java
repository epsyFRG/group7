package com.crm.group7.security;

import com.crm.group7.entities.Utente;
import com.crm.group7.exceptions.UnauthorizedException;
import com.crm.group7.service.UtenteService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UtenteService utenteService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Inserire il token nell'authorization header nel formato giusto!");
        }

        String accessToken = authHeader.replace("Bearer ", "");

        // 3. Verifichiamo se il token è valido. Se non è valido, lanciamo subito una 401
        jwtTools.verifyToken(accessToken);

        // ----------------------------------------------------------------------
        // 🚨 CORREZIONE CRUCIALE: ESTRAZIONE DEI RUOLI DAL TOKEN 🚨
        // ----------------------------------------------------------------------

        // 1. Estraiamo TUTTI i claims dal token
        Claims claims = jwtTools.extractAllClaims(accessToken);

        // 2. Estraiamo l'ID utente dal token (dal claim "sub")
        UUID utenteId = UUID.fromString(claims.getSubject());

        // 3. Cerchiamo l'utente nel db (OPZIONALE ma lo teniamo per avere l'oggetto completo)
        Utente found = utenteService.findById(utenteId);

        // 4. Estraiamo la stringa dei ruoli dal claim "roles" che abbiamo aggiunto
        String rolesString = claims.get("roles", String.class);
        List<SimpleGrantedAuthority> authorities = null;

        if (rolesString != null) {
            // Es. "ADMIN,UTENTE" -> [SimpleGrantedAuthority("ADMIN"), SimpleGrantedAuthority("UTENTE")]
            authorities = Arrays.stream(rolesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // 5. Associamo l'utente e le autorità ESTRATTE DAL TOKEN al Security Context
        // Usiamo le authorities estratte dal token (authorities), che ora contengono "ADMIN"
        Authentication authentication = new UsernamePasswordAuthenticationToken(found, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. Passiamo la richiesta al prossimo
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Ignora /auth/login e /auth/register
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}