package com.crm.group7.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FatturaRequestDTO {

    @NotNull(message = "Il numero della fattura è obbligatorio.")
    private Long numero;

    @NotNull(message = "L'importo totale è obbligatorio.")
    @DecimalMin(value = "0.01", message = "L'importo deve essere positivo.")
    private Double importo;

    @NotNull(message = "La data è obbligatoria.")
    @PastOrPresent(message = "La data della fattura non può essere nel futuro.")
    private LocalDate data;

    @NotNull(message = "L'ID del cliente è obbligatorio.")
    private UUID idCliente;

    @NotNull(message = "L'ID dello stato è obbligatorio.")
    private UUID idStato;

}