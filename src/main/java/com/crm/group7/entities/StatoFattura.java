package com.crm.group7.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stato_fattura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatoFattura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_stato")
    private UUID idStato;

    @Column(nullable = false, unique = true, length = 50)
    private String stato;

    @JsonIgnore
    @OneToMany(mappedBy = "idStato")
    private List<Fattura> fatture;

    public StatoFattura(String stato) {
        this.stato = stato;
    }
}
