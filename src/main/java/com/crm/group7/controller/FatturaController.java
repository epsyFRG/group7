package com.crm.group7.controller;

import com.crm.group7.entities.Fattura;
import com.crm.group7.service.FatturaService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/fattura")
public class FatturaController {

    @Autowired
    private FatturaService fatturaService;

    @GetMapping
    public Object filtroFattura(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID idCliente,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) UUID idStato,
            Pageable pageable
    ) {

        if (id != null) {
            Fattura fattura = fatturaService.findById(id);
            return fattura != null ? Collections.singletonList(fattura) : Collections.emptyList();
        } else if (idCliente != null) {
            return fatturaService.findByCliente(idCliente, pageable);
        } else if (idStato != null) {
            return fatturaService.findByStato(idStato, pageable);
        } else if (anno != null) {
            return fatturaService.findByAnno(anno, pageable);
        } else {
            return fatturaService.findAll(pageable);
        }
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fattura creaFattura(@RequestBody Fattura nuovaFattura){
        log.info("Creazione nuova fattura per cliente",nuovaFattura.getCliente());
        return fatturaService.save(nuovaFattura);
    }
    @PutMapping("/{id}")
      public Fattura aggiornaFattura(
              @PathVariable UUID id,
              @RequestBody Fattura fatturaAggiornata
    ){
        log.info("Aggiornamento fattura ocn ID",id);
        return  fatturaService.update(id,fatturaAggiornata);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimanFattura(@PathVariable UUID id){
        log.info("Richiesta eliminazione fattura con ID {}", id);
        fatturaService.delete(id);
    }
    @PutMapping("/{idFattura}/{stato}/{idNuovoStato}")
    public Fattura cambiaStato(
            @PathVariable UUID idFattura,
            @PathVariable UUID idNuovoStato
    ){
return  fatturaService.cambiaStato(idFattura,idNuovoStato);
    }
}
