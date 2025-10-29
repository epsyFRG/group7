package com.crm.group7.payloads;

import com.crm.group7.entities.enums.RagioneSociale;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ClienteDTO(
        @NotNull(message = "La ragione sociale è obbligatoria!")
        RagioneSociale ragioneSociale,

        @NotBlank(message = "La partita IVA è obbligatoria!")
        @Size(min = 11, max = 11, message = "La partita IVA deve contenere 11 cifre!")
        String partitaIva,

        @NotNull(message = "La data di inserimento è obbligatorIA!")
        LocalDate dataInserimento,

        @NotNull(message = "La data dell'ultimo contatto è obbligatorIA!")
        LocalDate dataUltimoContatto,

        @PositiveOrZero(message = "Il fatturato annuale non può essere negativo!")
        double fatturatoAnnuale,

        @Email(message = "Inserire una PEC valida!")
        @NotBlank(message = "La PEC è obbligatorIA!")
        String pec,

        @NotBlank(message = "Il numero di telefono è obbligatorio!")
        String telefono,

        @Email(message = "Inserire un'email di contatto valida!")
        @NotBlank(message = "L'email di contatto è obbligatorIA!")
        String emailContatto,

        @NotBlank(message = "Il nome del contatto è obbligatorio!")
        String nomeContatto,

        @NotBlank(message = "Il telefono del contatto è obbligatorio!")
        String telefonoContatto,

        String logoAziendale,
        
        @Valid // Valida anche gli oggetti DTO nella lista
        List<IndirizzoDTO> indirizzi
) {
}

