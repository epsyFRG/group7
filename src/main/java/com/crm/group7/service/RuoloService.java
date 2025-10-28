package com.crm.group7.service;


import com.crm.group7.entities.Ruolo;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.repositories.RuoloRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RuoloService {

    @Autowired
    private RuoloRepository ruoloRepository;

    public Ruolo save(Ruolo ruolo) {
        if (ruoloRepository.findByNome(ruolo.getNome()).isPresent()) {
            throw new BadRequestException("Il ruolo " + ruolo.getNome() + " esiste già");
        }
        Ruolo savedRuolo = ruoloRepository.save(ruolo);
        log.info("Ruolo '{}' creato con successo con l'id: {}", savedRuolo.getNome(), savedRuolo.getId());
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
        return ruoloRepository.findByNome(nome)
                .orElseThrow(() -> new NotFoundException("Ruolo '" + nome + "' non trovato"));
    }

    public Ruolo update(UUID id, Ruolo ruoloAggiornato) {
        Ruolo ruoloEsistente = this.findById(id);

        if (!ruoloEsistente.getNome().equals(ruoloAggiornato.getNome()) &&
                ruoloRepository.findByNome(ruoloAggiornato.getNome()).isPresent()) {
            throw new BadRequestException("Il ruolo '" + ruoloAggiornato.getNome() + "' esiste già");
        }

        ruoloEsistente.setNome(ruoloAggiornato.getNome());
        Ruolo ruoloModificato = ruoloRepository.save(ruoloEsistente);
        log.info("Ruolo con ID {} aggiornato a '{}'", id, ruoloModificato.getNome());
        return ruoloModificato;
    }

    public void delete(UUID id) {
        Ruolo ruolo = this.findById(id);

        if (!ruolo.getUtenti().isEmpty()) {
            throw new BadRequestException("Impossibile eliminare il ruolo '" + ruolo.getNome() +
                    "' perché è assegnato a " + ruolo.getUtenti().size() + " utenti");
        }

        ruoloRepository.delete(ruolo);
        log.info("Ruolo '{}' con ID {} eliminato con successo", ruolo.getNome(), id);
    }
}
