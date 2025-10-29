package com.crm.group7.payloads;

import java.util.UUID;

public record StatoFatturaResponseDTO(
        UUID idStato,
        String stato
) {
}
