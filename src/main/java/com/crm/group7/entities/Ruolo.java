package com.crm.group7.entities;

import com.crm.group7.entities.enums.Ruoli;
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
@ToString(exclude = {"utenti"})
@NoArgsConstructor

public class Ruolo {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Ruoli ruolo;
    
    private String nome;

    @ManyToMany(mappedBy = "ruoli")
    private List<Utente> utenti = new ArrayList<>();

    public Ruolo(Ruoli ruolo) {
        this.ruolo = ruolo;
        this.nome = ruolo != null ? ruolo.name() : null;
    }
}
