package com.crm.group7.service;

import com.crm.group7.entities.Fattura;
import com.crm.group7.entities.StatoFattura;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.repositories.FatturaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class FatturaService {

    @Autowired
    private FatturaRepository fatturaRepository;

    @Autowired
    private StatoFatturaService statoFatturaService;

    public Fattura save(Fattura fattura) {
        if (fatturaRepository.existsByNumero(fattura.getNumero())) {
            throw new BadRequestException("Esiste già una fattura con questo numero: " + fattura.getNumero());
        }
        if (fattura.getImporto() <= 0) {
            throw new BadRequestException("L'importo della fattura deve essere maggiore di zero");
        }

        Fattura savedFattura = fatturaRepository.save(fattura);
        log.info("La fattura numero {} è stata creata con successo per il cliente {} / Importo: €{}",
                savedFattura.getNumero(),
                savedFattura.getCliente().getIdCliente(),
                savedFattura.getImporto());
        return savedFattura;
    }

    public Page<Fattura> findAll(Pageable pageable) {
        return fatturaRepository.findAll(pageable);
    }

    public Fattura findById(UUID idFattura) {
        return fatturaRepository.findById(idFattura)
                .orElseThrow(() -> new NotFoundException("La fattura con l'id " + idFattura + " non è stata trovata"));
    }

    public Fattura update(UUID idFattura, Fattura fatturaAggiornata) {
        Fattura fatturaEsistente = this.findById(idFattura);
        if (!fatturaEsistente.getNumero().equals(fatturaAggiornata.getNumero()) &&
                fatturaRepository.existsByNumero(fatturaAggiornata.getNumero())) {
            throw new BadRequestException("Esiste già una fattura con numero: " + fatturaAggiornata.getNumero());
        }
        if (fatturaAggiornata.getImporto() <= 0) {
            throw new BadRequestException("L'importo della fattura deve essere maggiore di zero");
        }

        fatturaEsistente.setData(fatturaAggiornata.getData());
        fatturaEsistente.setImporto(fatturaAggiornata.getImporto());
        fatturaEsistente.setNumero(fatturaAggiornata.getNumero());
        fatturaEsistente.setCliente(fatturaAggiornata.getCliente());
        fatturaEsistente.setStato(fatturaAggiornata.getStato());

        Fattura fatturaModificata = fatturaRepository.save(fatturaEsistente);
        log.info("Fattura con ID {} aggiornata con successo", idFattura);
        return fatturaModificata;
    }

    public void delete(UUID idFattura) {
        Fattura fattura = this.findById(idFattura);
        fatturaRepository.delete(fattura);
        log.info("Fattura numero {} eliminata con successo", fattura.getNumero());
    }

    public Page<Fattura> findByFiltri(UUID idCliente, UUID idStato, LocalDate data, Integer anno,
                                      Double minImporto, Double maxImporto, Pageable pageable) {

        Specification<Fattura> spec = Specification.where(null);
        if (idCliente != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("cliente").get("idCliente"), idCliente));
        }
        if (idStato != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("stato").get("idStato"), idStato));
        }
        if (data != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("data"), data));
        }
        if (anno != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("YEAR", Integer.class, root.get("data")), anno));
        }
        if (minImporto != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("importo"), minImporto));
        }
        if (maxImporto != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("importo"), maxImporto));
        }

        Page<Fattura> risultato = fatturaRepository.findAll(spec, pageable);
        log.info("Trovate {} fatture con i filtri", risultato.getTotalElements());
        return risultato;
    }

    public Fattura cambiaStato(UUID idFattura, UUID idNuovoStato) {
        Fattura fattura = this.findById(idFattura);
        StatoFattura nuovoStato = statoFatturaService.findById(idNuovoStato);

        fattura.setStato(nuovoStato);
        Fattura fatturaAggiornata = fatturaRepository.save(fattura);
        log.info("Stato della fattura {} cambiato in '{}'", fattura.getNumero(), nuovoStato.getStato());
        return fatturaAggiornata;
    }

    public Page<Fattura> findByCliente(UUID idCliente, Pageable pageable) {
        return this.findByFiltri(idCliente, null, null, null, null, null, pageable);
    }

    public Page<Fattura> findByStato(UUID idStato, Pageable pageable) {
        return this.findByFiltri(null, idStato, null, null, null, null, pageable);
    }

    public Page<Fattura> findByAnno(Integer anno, Pageable pageable) {
        return this.findByFiltri(null, null, null, anno, null, null, pageable);
    }
}