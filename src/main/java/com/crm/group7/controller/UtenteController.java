package com.crm.group7.controller;

import com.crm.group7.entities.Utente;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.payloads.UtenteDTO;
import com.crm.group7.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Utente createUtente(@RequestBody @Validated UtenteDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Errori nel payload: " + messages);
        }
        return this.utenteService.save(payload);
    }

    @GetMapping("/me")
    public Utente getProfile(@AuthenticationPrincipal Utente currentAuthenticateUtente) {
        return currentAuthenticateUtente;
    }

    @GetMapping("/{idUtente}")
    public Utente findById(@PathVariable UUID idUtente) {
        return this.utenteService.findById(idUtente);
    }
}