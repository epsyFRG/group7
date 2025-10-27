package com.crm.group7.entities;

import com.crm.group7.entities.enums.TipoIndirizzo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Entity
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

    private String comune;

    @Enumerated(EnumType.STRING)
    private TipoIndirizzo tipoIndirizzo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
}
