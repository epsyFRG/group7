package com.crm.group7.service;

import com.crm.group7.entities.StatoFattura;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.repositories.StatoFatturaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class StatoFatturaService {

    @Autowired
    private StatoFatturaRepository statoFatturaRepository;

    public StatoFattura save(StatoFattura statoFattura) {
        if (statoFatturaRepository.existsByStato(statoFattura.getStato())) {
            throw new BadRequestException("Lo stato '" + statoFattura.getStato() + "' esiste già");
        }
        StatoFattura savedStato = statoFatturaRepository.save(statoFattura);
        log.info("Stato fattura '{}' creato con successo con ID: {}", savedStato.getStato(), savedStato.getIdStato());
        return savedStato;
    }

    public Page<StatoFattura> findAll(Pageable pageable) {
        return statoFatturaRepository.findAll(pageable);
    }

    public StatoFattura findById(UUID idStato) {
        return statoFatturaRepository.findById(idStato)
                .orElseThrow(() -> new NotFoundException("Stato fattura con ID " + idStato + " non trovato"));
    }

    public StatoFattura findByStato(String stato) {
        return statoFatturaRepository.findByStato(stato)
                .orElseThrow(() -> new NotFoundException("Stato fattura '" + stato + "' non trovato"));
    }

    public StatoFattura update(UUID idStato, StatoFattura statoFatturaAggiornato) {
        StatoFattura statoEsistente = this.findById(idStato);
        if (!statoEsistente.getStato().equals(statoFatturaAggiornato.getStato()) &&
                statoFatturaRepository.existsByStato(statoFatturaAggiornato.getStato())) {
            throw new BadRequestException("Lo stato '" + statoFatturaAggiornato.getStato() + "' esiste già");
        }

        statoEsistente.setStato(statoFatturaAggiornato.getStato());
        StatoFattura statoModificato = statoFatturaRepository.save(statoEsistente);
        log.info("Stato fattura con ID {} aggiornato a '{}'", idStato, statoModificato.getStato());
        return statoModificato;
    }

    public void delete(UUID idStato) {
        StatoFattura statoFattura = this.findById(idStato);
        statoFatturaRepository.delete(statoFattura);
        log.info("Stato fattura con ID {} eliminato con successo", idStato);
    }
}