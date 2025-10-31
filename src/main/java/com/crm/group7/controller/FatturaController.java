package com.crm.group7.controller;

import com.crm.group7.entities.Fattura;
import com.crm.group7.payloads.FatturaRequestDTO;
import com.crm.group7.payloads.FatturaResponseDTO;
import com.crm.group7.service.EmailService;
import com.crm.group7.service.FatturaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fatture")
public class FatturaController {
    @Autowired
    private FatturaService fatturaService;
    @Autowired
    private EmailService emailService;

    // 1. LETTURA CON FILTRI e PAGINAZIONE (Ritorna Page di DTO)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public Page<FatturaResponseDTO> getFattureConFiltri(
            @RequestParam(required = false) UUID idCliente,
            @RequestParam(required = false) UUID idStato,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) Double minImporto,
            @RequestParam(required = false) Double maxImporto,
            Pageable pageable
    ) {
        return fatturaService.findByFiltri(
                idCliente,
                idStato,
                data,
                anno,
                minImporto,
                maxImporto,
                pageable
        );
    }

    // 2. LETTURA SINGOLA PER ID (Ritorna DTO)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public FatturaResponseDTO getFatturaById(@PathVariable UUID id) {
        return fatturaService.findById(id);
    }

    // 3. CREAZIONE (Solo ADMIN - Usa Request DTO e @Valid)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public FatturaResponseDTO creaFattura(@RequestBody @Valid FatturaRequestDTO nuovaFatturaDTO) {
        FatturaResponseDTO nuovaFattura = fatturaService.save(nuovaFatturaDTO);

        // Recupera l'indirizzo email del cliente dalla fattura appena creata
        String emailCliente = nuovaFattura.getEmailCliente();

        if (emailCliente != null) {
            // Recupera il codice o numero identificativo della fattura
            String codiceFattura = nuovaFattura.getNumero();

            // Invia la notifica via email
            emailService.sendMailgunEmail(
                    emailCliente,
                    "Conferma Emissione Fattura " + codiceFattura,
                    "Gentile Cliente, la tua fattura è stata emessa con successo."
            );
        } else {
            log.warn("Email del cliente non disponibile per l'invio della notifica di fattura {}", nuovaFattura.getNumero());
        }

        return nuovaFattura;
    }

    // 4. AGGIORNAMENTO
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FatturaResponseDTO aggiornaFattura(
            @PathVariable UUID id,
            @RequestBody Fattura fatturaAggiornata
    ) {
        log.info("Aggiornamento fattura con ID {}", id);
        return fatturaService.update(id, fatturaAggiornata);
    }

    // 5. CANCELLAZIONE (Solo ADMIN)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void eliminaFattura(@PathVariable UUID id) {
        log.info("Richiesta eliminazione fattura con ID {}", id);
        fatturaService.delete(id);
    }

    // 6. CAMBIA STATO (PATCH - Ritorna DTO)
    @PatchMapping("/{idFattura}/stato/{idNuovoStato}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FatturaResponseDTO cambiaStato( // TIPO DI RITORNO MODIFICATO
                                           @PathVariable UUID idFattura,
                                           @PathVariable UUID idNuovoStato
    ) {
        return fatturaService.cambiaStato(idFattura, idNuovoStato);
    }
}