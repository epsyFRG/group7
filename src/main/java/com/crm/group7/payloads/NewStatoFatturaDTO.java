package com.crm.group7.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NewStatoFatturaDTO {
    @NotBlank(message = "Lo stato della fattura è obbligatorio")
    @Size(min = 2, max = 50, message = "Lo stato deve essere tra 2 e 50 caratteri")
    private String stato;
}
