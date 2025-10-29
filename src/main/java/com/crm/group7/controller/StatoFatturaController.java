package com.crm.group7.controller;

import com.crm.group7.entities.StatoFattura;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.payloads.NewStatoFatturaDTO;
import com.crm.group7.payloads.StatoFatturaResponseDTO;
import com.crm.group7.service.StatoFatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stati-fattura")
public class StatoFatturaController {

    @Autowired
    private StatoFatturaService statoFatturaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public Page<StatoFatturaResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stato") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return statoFatturaService.findAll(pageable).map(stato ->
                new StatoFatturaResponseDTO(stato.getIdStato(), stato.getStato())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public StatoFatturaResponseDTO findById(@PathVariable UUID id) {
        StatoFattura stato = statoFatturaService.findById(id);
        return new StatoFatturaResponseDTO(stato.getIdStato(), stato.getStato());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public StatoFatturaResponseDTO save(@RequestBody @Validated NewStatoFatturaDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            String messages = validation.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Errori nel payload: " + messages);
        }
        StatoFattura stato = new StatoFattura(body.getStato());
        StatoFattura savedStato = statoFatturaService.save(stato);
        return new StatoFatturaResponseDTO(savedStato.getIdStato(), savedStato.getStato());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public StatoFatturaResponseDTO update(@PathVariable UUID id, @RequestBody @Validated NewStatoFatturaDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            String messages = validation.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Errori nel payload: " + messages);
        }
        StatoFattura statoAggiornato = new StatoFattura(body.getStato());
        StatoFattura updatedStato = statoFatturaService.update(id, statoAggiornato);
        return new StatoFatturaResponseDTO(updatedStato.getIdStato(), updatedStato.getStato());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID id) {
        statoFatturaService.delete(id);
    }
}
