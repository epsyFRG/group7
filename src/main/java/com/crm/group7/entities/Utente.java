package com.crm.group7.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Utente")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Utente implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    private String nome;
    private String cognome;
    private String avatarURL;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "RuoloUtente",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRuolo")
    )
    private List<Ruolo> ruoli = new ArrayList<>();

    public Utente(String email, String password, String nome, String cognome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Questo metodo vogliamo che restituisca una lista di Authorities, cioè dei ruoli dell'utente
        // SimpleGrantedAuthority è una classe che implementa GrantedAuthority e ci serve per convertire il ruolo dell'utente
        // che nel nostro caso è un enum in un oggetto utilizzabile dai meccanismi di Spring Security
        return List.of(new SimpleGrantedAuthority(this.ruoli.name()));
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}

