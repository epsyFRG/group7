package com.crm.group7.entities;

import com.crm.group7.entities.enums.RagioneSociale;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Cliente {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID idCliente;
    @Enumerated(EnumType.STRING)
    private RagioneSociale ragioneSociale;
    private String partitaIva;
    private LocalDate dataInserimento;
    private LocalDate dataUltimoContatto;
    private double fatturatoAnnuale;
    private String pec;
    private String telefono;
    private String emailContatto;
    private String nomeContatto;
    private String telefonoContatto;
    private String logoAziendale;
    @ManyToOne
    @JoinColumn(name = "id_utente")
    private Utente utente;

    public Cliente(RagioneSociale ragioneSociale, String partitaIva, LocalDate dataInserimento, LocalDate dataUltimoContatto, double fatturatoAnnuale, String pec, String telefono, String emailContatto, String nomeContatto, String telefonoContatto) {
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
        this.dataInserimento = dataInserimento;
        this.dataUltimoContatto = dataUltimoContatto;
        this.fatturatoAnnuale = fatturatoAnnuale;
        this.pec = pec;
        this.telefono = telefono;
        this.emailContatto = emailContatto;
        this.nomeContatto = nomeContatto;
        this.telefonoContatto = telefonoContatto;
    }
}
