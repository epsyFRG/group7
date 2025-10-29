package com.crm.group7.payloads;

import com.crm.group7.entities.enums.TipoIndirizzo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IndirizzoDTO(
        @NotBlank(message = "Inserire la via è obbligatorio!")
        String via,

        @NotNull(message = "Inserire il civico è obbligatorio!")
        @Min(value = 1, message = "Il numero civico deve essere positivo!")
        Integer civico,

        @NotBlank(message = "Inserire la località è obbligatorio!")
        String localita,

        @NotNull(message = "Inserire il cap è obbligatorio!")
        @Min(value = 10000, message = "Il CAP deve contenere 5 cifre!")
        @Max(value = 99999, message = "Il CAP deve contenere 5 cifre!")
        Integer cap,

        @NotNull(message = "Il tipo di indirizzo è obbligatorio!")
        TipoIndirizzo tipoIndirizzo,

        @NotNull(message = "L'ID del comune è obbligatorio per salvare l'indirizzo!")
        UUID comuneId // L'ID del comune a cui questo indirizzo è associato
) {
}