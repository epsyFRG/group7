package com.crm.group7.controller;

import com.crm.group7.entities.Utente;
import com.crm.group7.exceptions.ValidationException;
import com.crm.group7.payloads.LoginDTO;
import com.crm.group7.payloads.LoginResponseDTO;
import com.crm.group7.payloads.UtenteDTO;
import com.crm.group7.service.AuthService;
import com.crm.group7.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UtenteService utenteService;

    // POST http://localhost:3001/auth/login
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO body) {
        return new LoginResponseDTO(authService.checkCredenzialiEGeneraToken(body));
    }

    // POST http://localhost:3001/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente createUtente(@RequestBody @Validated UtenteDTO payload, BindingResult validationResult) {
        // @Validated serve per "attivare" la validazione
        // BindingResult è un oggetto che contiene tutti gli errori e anche dei metodi comodi da usare tipo .hasErrors()
        if (validationResult.hasErrors()) {

            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.utenteService.save(payload);
    }
}
