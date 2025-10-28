package com.crm.group7.payloads;

import com.crm.group7.entities.enums.TipoIndirizzo;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NewIndirizzoDTO {
    @NotBlank(message = "Inserire la via è obbligatorio!")
    private String via;

    @NotNull(message = "Inserire il civico è obbligatorio!")
    private int civico;

    @NotBlank(message = "Inserire la località è obbligatorio!")
    private String localita;

    @NotNull(message = "Inserire il cap è obbligatorio!")
    @Min(value = 10000, message = "Il CAP deve contenere 5 cifre!")
    @Max(value = 99999, message = "Il CAP deve contenere 5 cifre!")
    private int cap;

    @NotNull(message = "Il tipo di indirizzo è obbligatorio!")
    private TipoIndirizzo tipoIndirizzo;
}
