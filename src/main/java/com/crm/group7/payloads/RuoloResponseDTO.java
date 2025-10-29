package com.crm.group7.payloads;

import java.util.UUID;

public record RuoloResponseDTO(
        UUID id,
        String nome
) {
}