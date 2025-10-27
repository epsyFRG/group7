package com.crm.group7.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Ruolo")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class Ruolo {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "ruoli")
    private List<Utente> utenti = new ArrayList<>();

    public Ruolo(String nome) {
        this.nome = nome;
    }
}
