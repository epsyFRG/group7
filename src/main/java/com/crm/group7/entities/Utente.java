package com.crm.group7.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.util.stream.Collectors;

@Entity
@Table(name = "Utente")
@Getter
@Setter
@ToString(exclude = {"ruoli"})
@NoArgsConstructor
@JsonIgnoreProperties({"password", "authorities", "enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired"})
public class Utente implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String nome;
    private String cognome;
    private String avatarURL;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "RuoloUtente",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRuolo")
    )
    @JsonIgnore
    private List<Ruolo> ruoli = new ArrayList<>();

    public Utente(String username, String email, String password, String nome, String cognome) {
        this.username = username;
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
        return this.ruoli.stream()
                .map(ruolo -> new SimpleGrantedAuthority(ruolo.getRuolo().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}

