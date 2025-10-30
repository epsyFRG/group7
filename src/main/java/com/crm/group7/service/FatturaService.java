package com.crm.group7.service;

import com.crm.group7.entities.Cliente;
import com.crm.group7.entities.Fattura;
import com.crm.group7.entities.StatoFattura;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.payloads.FatturaRequestDTO;
import com.crm.group7.payloads.FatturaResponseDTO;
import com.crm.group7.repositories.FatturaRepository;
import jakarta.persistence.criteria.Expression;
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
    @Autowired
    private ClienteService clienteService;

    public FatturaResponseDTO save(FatturaRequestDTO dto) {

        if (fatturaRepository.existsByNumero(dto.getNumero())) {
            throw new BadRequestException("Esiste già una fattura con questo numero: " + dto.getNumero());
        }

        Cliente cliente = clienteService.findClienteById(dto.getIdCliente());
        StatoFattura stato = statoFatturaService.findById(dto.getIdStato());

        // Mappatura DTO -> Entità
        Fattura nuovaFattura = new Fattura();
        nuovaFattura.setNumero(dto.getNumero());
        nuovaFattura.setImporto(dto.getImportoTotale());
        nuovaFattura.setData(dto.getData());
        nuovaFattura.setCliente(cliente);
        nuovaFattura.setStato(stato);

        Fattura savedFattura = fatturaRepository.save(nuovaFattura);
        log.info("La fattura numero {} è stata creata con successo.", savedFattura.getNumero());

        return mapToResponseDTO(savedFattura);
    }

    public FatturaResponseDTO findById(UUID idFattura) {
        Fattura fattura = this.findFatturaEntityById(idFattura);
        return mapToResponseDTO(fattura);
    }

    public FatturaResponseDTO update(UUID idFattura, Fattura fatturaAggiornata) {
        Fattura fatturaEsistente = this.findFatturaEntityById(idFattura);

        // La logica di validazione rimane
        if (!fatturaEsistente.getNumero().equals(fatturaAggiornata.getNumero()) &&
                fatturaRepository.existsByNumero(fatturaAggiornata.getNumero())) {
            throw new BadRequestException("Esiste già una fattura con numero: " + fatturaAggiornata.getNumero());
        }
        if (fatturaAggiornata.getImporto() <= 0) {
            throw new BadRequestException("L'importo della fattura deve essere maggiore di zero");
        }

        // Aggiornamento campi (senza toccare l'ID)
        fatturaEsistente.setData(fatturaAggiornata.getData());
        fatturaEsistente.setImporto(fatturaAggiornata.getImporto());
        fatturaEsistente.setNumero(fatturaAggiornata.getNumero());
        fatturaEsistente.setCliente(fatturaAggiornata.getCliente());
        fatturaEsistente.setStato(fatturaAggiornata.getStato());

        Fattura fatturaModificata = fatturaRepository.save(fatturaEsistente);
        log.info("Fattura con ID {} aggiornata con successo", idFattura);

        return mapToResponseDTO(fatturaModificata);
    }

    public void delete(UUID idFattura) {
        Fattura fattura = this.findFatturaEntityById(idFattura);
        fatturaRepository.delete(fattura);
        log.info("Fattura numero {} eliminata con successo", fattura.getNumero());
    }

    public FatturaResponseDTO cambiaStato(UUID idFattura, UUID idNuovoStato) {
        Fattura fattura = this.findFatturaEntityById(idFattura);
        StatoFattura nuovoStato = statoFatturaService.findById(idNuovoStato);

        fattura.setStato(nuovoStato);
        Fattura fatturaAggiornata = fatturaRepository.save(fattura);
        log.info("Stato della fattura {} cambiato in '{}'", fattura.getNumero(), nuovoStato.getStato());

        return mapToResponseDTO(fatturaAggiornata);
    }

    public Page<FatturaResponseDTO> findByFiltri(UUID idCliente, UUID idStato, LocalDate data, Integer anno,
                                                 Double minImporto, Double maxImporto, Pageable pageable) {

        // Inizializza una Specification "vuota" che non filtra nulla
        Specification<Fattura> spec = Specification.allOf();

        if (idCliente != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cliente").get("idCliente"), idCliente));
        }
        if (idStato != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("stato").get("idStato"), idStato));
        }
        if (data != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("data"), data));
        }

        if (anno != null) {
            spec = spec.and((root, query, cb) -> {
                // Usiamo la funzione "date_part" specifica di PostgreSQL
                Expression<Integer> yearExpression = cb.function(
                        "date_part",
                        Integer.class,
                        cb.literal("year"), // Argomento 1: l'unità di tempo
                        root.get("data")     // Argomento 2: la colonna
                );
                return cb.equal(yearExpression, anno);
            });
        }

        if (minImporto != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("importo"), minImporto));
        }
        if (maxImporto != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("importo"), maxImporto));
        }

        Page<Fattura> risultatoEntita = fatturaRepository.findAll(spec, pageable);
        log.info("Trovate {} fatture con i filtri", risultatoEntita.getTotalElements());

        // Mappa i risultati della pagina in DTO
        return risultatoEntita.map(this::mapToResponseDTO);
    }


    private Fattura findFatturaEntityById(UUID idFattura) {
        return fatturaRepository.findById(idFattura)
                .orElseThrow(() -> new NotFoundException("La fattura con l'id " + idFattura + " non è stata trovata"));
    }


    private FatturaResponseDTO mapToResponseDTO(Fattura fattura) {

        Cliente cliente = fattura.getCliente();
        UUID idCliente = null;
        String nomeCliente = null; // Valore di default
        String emailContatto = null;

        if (cliente != null) {
            idCliente = cliente.getIdCliente();
            // Controlliamo che ragioneSociale non sia null prima di chiamare .name()
            if (cliente.getRagioneSociale() != null) {
                nomeCliente = cliente.getRagioneSociale().name();
            }

            emailContatto = cliente.getEmailContatto();
        }

        return FatturaResponseDTO.builder()
                .idFattura(fattura.getIdFattura())
                .numero(String.valueOf(fattura.getNumero()))
                .data(fattura.getData())
                .importoTotale(fattura.getImporto())

                // Dettagli Cliente
                .idCliente(idCliente)
                .emailCliente(emailContatto)

                // Dettagli Stato
                .idStato(fattura.getStato().getIdStato())

                .build();
    }
}

