package com.crm.group7.service;


import com.crm.group7.entities.Ruolo;
import com.crm.group7.entities.enums.Ruoli;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.repositories.RuoloRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class RuoloService {

    @Autowired
    private RuoloRepository ruoloRepository;

    public Ruolo save(Ruolo ruolo) {
        if (ruoloRepository.findByRuolo(ruolo.getRuolo()).isPresent()) {
            throw new BadRequestException("Il ruolo " + ruolo.getRuolo() + " esiste già");
        }
        Ruolo savedRuolo = ruoloRepository.save(ruolo);
        log.info("Ruolo '{}' creato con successo con l'id: {}", savedRuolo.getRuolo(), savedRuolo.getId());
        return savedRuolo;
    }

    public Page<Ruolo> findAll(Pageable pageable) {

        return ruoloRepository.findAll(pageable);
    }

    public Ruolo findById(UUID id) {

        return ruoloRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruolo con l'id " + id + " non trovato"));
    }

    public Ruolo findByNome(String nome) {
        Ruoli ruolo;
        ruolo = Ruoli.valueOf(nome.toUpperCase());
        return ruoloRepository.findByRuolo(ruolo)
                .orElseThrow(() -> new NotFoundException("Ruolo '" + nome + "' non trovato"));
    }

    public Ruolo update(UUID id, Ruolo ruoloAggiornato) {
        Ruolo ruoloEsistente = this.findById(id);

        if (!ruoloEsistente.getRuolo().equals(ruoloAggiornato.getRuolo()) &&
                ruoloRepository.findByRuolo(ruoloAggiornato.getRuolo()).isPresent()) {
            throw new BadRequestException("Il ruolo '" + ruoloAggiornato.getRuolo() + "' esiste già");
        }

        ruoloEsistente.setRuolo(ruoloAggiornato.getRuolo());
        Ruolo ruoloModificato = ruoloRepository.save(ruoloEsistente);
        log.info("Ruolo con ID {} aggiornato a '{}'", id, ruoloModificato.getRuolo());
        return ruoloModificato;
    }

    public void delete(UUID id) {
        Ruolo ruolo = this.findById(id);

        if (!ruolo.getUtenti().isEmpty()) {
            throw new BadRequestException("Impossibile eliminare il ruolo '" + ruolo.getRuolo() +
                    "' perché è assegnato a " + ruolo.getUtenti().size() + " utenti");
        }

        ruoloRepository.delete(ruolo);
        log.info("Ruolo '{}' con ID {} eliminato con successo", ruolo.getRuolo(), id);
    }
}
