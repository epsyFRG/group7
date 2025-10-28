package com.crm.group7.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "comune", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome", "provincia_id"})
})
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comune {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "provincia_id", nullable = false)
    private Provincia provincia;

    private String codiceProvinciaStorico;

    private String progressivoComune;
}
