package com.crm.group7.entities;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.UUID;

@Entity
@Table(name="Ruolo")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class Ruolo {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
  private  String nome;

    @OneToMany(mappedBy = "ruolo",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Utente>utenti;

    public Ruolo(String nome) {
        this.nome = nome;
    }
}
