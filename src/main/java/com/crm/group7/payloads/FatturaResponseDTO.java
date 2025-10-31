package com.crm.group7.payloads;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class FatturaResponseDTO {

    private UUID idFattura;
    private String numero;
    private LocalDate data;
    private Double importo;

    private UUID idCliente;
    private String emailCliente;

    private UUID idStato;

}