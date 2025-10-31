package com.crm.group7.controller;

import com.crm.group7.entities.Cliente;
import com.crm.group7.payloads.ClienteDTO;
import com.crm.group7.service.ClienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/clienti")
@Validated
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public Cliente saveCliente(@RequestBody @Valid ClienteDTO body) {
        return clienteService.saveCliente(body);
    }

    @GetMapping("/{clienteId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public Cliente getClienteById(
            @PathVariable @NotNull UUID clienteId
    ) {
        return clienteService.findClienteById(clienteId);
    }

    @PutMapping("/{clienteId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cliente findClienteAndUpdate(
            @PathVariable @NotNull UUID clienteId,
            @RequestBody @Valid ClienteDTO body
    ) {
        return clienteService.findClienteAndUpdate(clienteId, body);
    }

    @DeleteMapping("/{clienteId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findClienteAndDelete(
            @PathVariable @NotNull UUID clienteId
    ) {
        clienteService.findClienteAndDelete(clienteId);
    }

    @PatchMapping("/{clienteId}/logo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cliente uploadLogo(
            @PathVariable @NotNull UUID clienteId,
            @RequestParam("logo") MultipartFile file
    ) {
        return clienteService.uploadLogo(clienteId, file);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'UTENTE')")
    public Page<Cliente> getClientiConFiltri(

            // Filtro
            @RequestParam(required = false) Double minFatturato,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInserimentoAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataUltimoContattoBefore,
            @RequestParam(required = false) String nomeContains,

            // Paginazione
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            // Ordinamento
            @RequestParam(defaultValue = "idCliente") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        return clienteService.getClientiFiltratiEOrdinati(
                nomeContains,
                minFatturato,
                dataInserimentoAfter,
                dataUltimoContattoBefore,
                page,
                size,
                sortBy,
                sortDir
        );
    }
}