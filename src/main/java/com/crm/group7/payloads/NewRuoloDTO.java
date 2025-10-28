package com.crm.group7.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NewRuoloDTO {
    @NotBlank(message = "Il nome del ruolo e obbligatorio")
    private String nome;
}