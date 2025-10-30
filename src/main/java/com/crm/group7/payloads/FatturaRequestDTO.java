package com.crm.group7.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FatturaRequestDTO {

    @NotNull(message = "Il numero della fattura è obbligatorio.")
    private Long numero;

    @NotNull(message = "L'importo totale è obbligatorio.")
    @DecimalMin(value = "0.01", message = "L'importo deve essere positivo.")
    private Double importoTotale;

    @NotNull(message = "La data è obbligatoria.")
    private LocalDate data;

    @NotNull(message = "L'ID del cliente è obbligatorio.")
    private UUID idCliente;

    @NotNull(message = "L'ID dello stato è obbligatorio.")
    private UUID idStato;

}