package com.crm.group7.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fattura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fattura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_fattura")
    private UUID idFattura;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Double importo;

    @Column(nullable = false, unique = true)
    private Long numero;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_stato", nullable = false)
    private StatoFattura stato;

    public Fattura(LocalDate data, Double importo, Long numero, Cliente cliente, StatoFattura stato) {
        this.data = data;
        this.importo = importo;
        this.numero = numero;
        this.cliente = cliente;
        this.stato = stato;
    }
}
