package com.crm.group7.controller;

import com.crm.group7.entities.Ruolo;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.payloads.NewRuoloDTO;
import com.crm.group7.payloads.RuoloResponseDTO;
import com.crm.group7.service.RuoloService;
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
@RequestMapping("/ruoli")
public class RuoloController {

    @Autowired
    private RuoloService ruoloService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Page<RuoloResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ruoloService.findAll(pageable).map(ruolo ->
                new RuoloResponseDTO(ruolo.getId(), ruolo.getRuolo().name())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public RuoloResponseDTO findById(@PathVariable UUID id) {
        Ruolo ruolo = ruoloService.findById(id);
        return new RuoloResponseDTO(ruolo.getId(), ruolo.getRuolo().name());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public RuoloResponseDTO save(@RequestBody @Validated NewRuoloDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            String messages = validation.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Errori nel payload: " + messages);
        }
        Ruolo ruolo = new Ruolo(body.getNome());
        Ruolo savedRuolo = ruoloService.save(ruolo);
        return new RuoloResponseDTO(savedRuolo.getId(), savedRuolo.getRuolo());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RuoloResponseDTO update(@PathVariable UUID id, @RequestBody @Validated NewRuoloDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            String messages = validation.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Errori nel payload: " + messages);
        }
        Ruolo ruoloAggiornato = new Ruolo(body.getNome());
        Ruolo updatedRuolo = ruoloService.update(id, ruoloAggiornato);
        return new RuoloResponseDTO(updatedRuolo.getId(), updatedRuolo.getNome());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable UUID id) {
        ruoloService.delete(id);
    }
}