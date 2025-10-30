package com.crm.group7.security;

import com.crm.group7.entities.Utente;
import com.crm.group7.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String secret;

    public String createToken(Utente utente) {

        String ruoliString = utente.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .subject(String.valueOf(utente.getId()))

                .claim("roles", ruoliString)

                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public void verifyToken(String accessToken) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(accessToken);
            // parse() ci può lanciare diverse eccezioni, a seconda del problema che ha il token
            // ci lancerà una se il token è scaduto, un'altra se il token è stato manipolato, un'altra ancora se il token è malformato..
            // a noi non importà granché il tipo di eccezione, convertiamo tutte in --> 401
        } catch (Exception ex) {
            throw new UnauthorizedException("Ci sono stati errori nel token! Effettua di nuovo il login!");
        }
    }

    public UUID extractIdFromToken(String accessToken) {
        return UUID.fromString(Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject());
    }

    public Claims extractAllClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (Exception ex) {
            // Se il token è invalido o scaduto, questa eccezione verrà lanciata
            throw new UnauthorizedException("Problema con l'estrazione dei claims dal token: " + ex.getMessage());
        }
    }
}
