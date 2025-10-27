package com.crm.group7.entities;

import com.crm.group7.entities.enums.TipoIndirizzo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "indirizzo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_cliente", "tipo_indirizzo"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Indirizzo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String via;
    private int civico;
    private String localita;

    @Min(10000)
    @Max(99999)
    private int cap;

    @ManyToOne
    @JoinColumn(name = "comune_id", nullable = false)
    private Comune comune;

    @Enumerated(EnumType.STRING)
    private TipoIndirizzo tipoIndirizzo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
}
