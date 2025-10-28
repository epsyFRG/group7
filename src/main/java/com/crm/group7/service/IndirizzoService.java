package com.crm.group7.service;

import com.crm.group7.entities.Cliente;
import com.crm.group7.entities.Indirizzo;
import com.crm.group7.entities.enums.TipoIndirizzo;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.repositories.IndirizzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IndirizzoService {
    @Autowired
    private IndirizzoRepository indirizzoRepository;

    public IndirizzoService(IndirizzoRepository indirizzoRepository) {
        this.indirizzoRepository = indirizzoRepository;
    }

    // CREATE <- creazione di un nuovo indirizzo
    public Indirizzo saveIndirizzo(Indirizzo indirizzo) {
        Cliente cliente = indirizzo.getCliente();
        TipoIndirizzo tipoIndirizzo = indirizzo.getTipoIndirizzo();

        Optional<Indirizzo> existing = indirizzoRepository.findByClienteAndTipoIndirizzo(cliente, tipoIndirizzo);
        if (existing.isPresent() && !existing.get().getId().equals(indirizzo.getId())) {
            throw new BadRequestException("Questo indirizzo in via " + indirizzo.getVia() + " è già in uso!");
        }

        return indirizzoRepository.save(indirizzo);
    }

    // READ <- paginazione e ricerca per ID
    public Page<Indirizzo> getIndirizzi(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return indirizzoRepository.findAll(pageable);
    }

    public Indirizzo findIndirizzoById(UUID indirizzoId) {
        return indirizzoRepository.findById(indirizzoId).orElseThrow(() -> new NotFoundException(indirizzoId));
    }

    // UPDATE <- modifica di un indirizzo specifico tramite ID
    public Indirizzo findIndirizzoAndUpdate(UUID indirizzoId, Indirizzo payload) {
        Indirizzo found = this.findIndirizzoById(indirizzoId);

        found.setVia(payload.getVia());
        found.setCivico(payload.getCivico());
        found.setLocalita(payload.getLocalita());
        found.setCap(payload.getCap());
        found.setTipoIndirizzo(payload.getTipoIndirizzo());

        return indirizzoRepository.save(found);
    }

    // DELETE <- elimina un indirizzo specifico tramite ID
    public void findIndirizzoAndDelete(UUID indirizzoId) {
        Indirizzo found = this.findIndirizzoById(indirizzoId);
        indirizzoRepository.delete(found);
    }
}
