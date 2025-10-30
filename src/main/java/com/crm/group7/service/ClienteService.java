package com.crm.group7.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.crm.group7.entities.Cliente;
import com.crm.group7.entities.Comune;
import com.crm.group7.entities.Indirizzo;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.payloads.ClienteDTO;
import com.crm.group7.repositories.ClienteRepository;
import com.crm.group7.repositories.ComuneRepository;
import com.crm.group7.repositories.IndirizzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private IndirizzoRepository indirizzoRepository;

    @Autowired
    private ComuneRepository comuneRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public Cliente saveCliente(ClienteDTO dto) {

        if (clienteRepository.existsByPartitaIva(dto.partitaIva())) {
            throw new BadRequestException("Esiste già un cliente con questa Partita IVA: " + dto.partitaIva());
        }
        if (clienteRepository.existsByPec(dto.pec())) {
            throw new BadRequestException("Questa PEC è già registrata: " + dto.pec());
        }
        if (clienteRepository.existsByEmailContatto(dto.emailContatto())) {
            throw new BadRequestException("Questa Email Contatto è già registrata: " + dto.emailContatto());
        }

        Cliente cliente = mapToEntity(dto);
        Cliente savedCliente = clienteRepository.save(cliente);

        if (dto.indirizzi() != null && !dto.indirizzi().isEmpty()) {
            List<Indirizzo> nuoviIndirizzi = dto.indirizzi().stream()
                    .map(indirizzoDTO -> {
                        Comune comune = comuneRepository.findById(indirizzoDTO.comuneId())
                                .orElseThrow(() -> new NotFoundException("Comune con id " + indirizzoDTO.comuneId() + " non trovato."));

                        Indirizzo indirizzo = new Indirizzo();
                        indirizzo.setVia(indirizzoDTO.via());
                        indirizzo.setCivico(indirizzoDTO.civico());
                        indirizzo.setLocalita(indirizzoDTO.localita());
                        indirizzo.setCap(indirizzoDTO.cap());
                        indirizzo.setTipoIndirizzo(indirizzoDTO.tipoIndirizzo()); // Corretto
                        indirizzo.setComune(comune);
                        indirizzo.setCliente(savedCliente);
                        return indirizzo;
                    })
                    .collect(Collectors.toList());

            indirizzoRepository.saveAll(nuoviIndirizzi);
            savedCliente.setIndirizzi(nuoviIndirizzi);
        }

        return savedCliente;
    }

    public Cliente findClienteById(UUID clienteId) {
        return clienteRepository.findById(clienteId).orElseThrow(() -> new NotFoundException(clienteId));
    }

    @Transactional
    public Cliente findClienteAndUpdate(UUID clienteId, ClienteDTO payload) {
        Cliente found = findClienteById(clienteId);

        if (clienteRepository.existsByPartitaIvaAndIdClienteNot(payload.partitaIva(), clienteId)) {
            throw new BadRequestException("Esiste già un ALTRO cliente con questa Partita IVA: " + payload.partitaIva());
        }
        if (clienteRepository.existsByPecAndIdClienteNot(payload.pec(), clienteId)) {
            throw new BadRequestException("Esiste già un ALTRO cliente con questa PEC: " + payload.pec());
        }
        if (clienteRepository.existsByEmailContattoAndIdClienteNot(payload.emailContatto(), clienteId)) {
            throw new BadRequestException("Esiste già un ALTRO cliente con questa Email Contatto: " + payload.emailContatto());
        }

        found.setRagioneSociale(payload.ragioneSociale());
        found.setPartitaIva(payload.partitaIva());
        found.setDataInserimento(payload.dataInserimento());
        found.setDataUltimoContatto(payload.dataUltimoContatto());
        found.setFatturatoAnnuale(payload.fatturatoAnnuale());
        found.setPec(payload.pec());
        found.setTelefono(payload.telefono());
        found.setEmailContatto(payload.emailContatto());
        found.setNomeContatto(payload.nomeContatto());
        found.setTelefonoContatto(payload.telefonoContatto());
        // Il logoAziendale viene aggiornato solo tramite l'endpoint /logo

        // 1. Pulisci la collezione GESTITA
        found.getIndirizzi().clear();

        // 2. FORZA IL FLUSH SUL DATABASE
        // Questo esegue immediatamente le DELETE causate da orphanRemoval
        clienteRepository.saveAndFlush(found);

        // 3. Ora che il database è pulito, aggiungi i nuovi indirizzi
        if (payload.indirizzi() != null && !payload.indirizzi().isEmpty()) {

            List<Indirizzo> nuoviIndirizzi = payload.indirizzi().stream()
                    .map(indirizzoDTO -> {
                        Comune comune = comuneRepository.findById(indirizzoDTO.comuneId())
                                .orElseThrow(() -> new NotFoundException("Comune con id " + indirizzoDTO.comuneId() + " non trovato."));

                        Indirizzo indirizzo = new Indirizzo();
                        indirizzo.setVia(indirizzoDTO.via());
                        indirizzo.setCivico(indirizzoDTO.civico());
                        indirizzo.setLocalita(indirizzoDTO.localita());
                        indirizzo.setCap(indirizzoDTO.cap());
                        indirizzo.setTipoIndirizzo(indirizzoDTO.tipoIndirizzo());
                        indirizzo.setComune(comune);
                        indirizzo.setCliente(found);
                        return indirizzo;
                    })
                    .collect(Collectors.toList());

            // 4. Aggiungi i nuovi alla collezione.
            found.getIndirizzi().addAll(nuoviIndirizzi);
        }

        return clienteRepository.save(found);
    }

    public void findClienteAndDelete(UUID clienteId) {
        Cliente found = findClienteById(clienteId);

        indirizzoRepository.deleteByCliente(found);

        clienteRepository.delete(found);
    }

    public Cliente uploadLogo(UUID clienteId, MultipartFile file) {
        Cliente cliente = findClienteById(clienteId);
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String logoUrl = uploadResult.get("secure_url").toString();
            cliente.setLogoAziendale(logoUrl);
            return clienteRepository.save(cliente);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'upload dell'immagine", e);
        }
    }

    public Page<Cliente> getClientiFiltratiEOrdinati(
            String nome,
            Double fatturatoMin,
            LocalDate dataInsAfter,
            LocalDate dataUltContBefore,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Caso speciale per provincia
        if ("provincia".equalsIgnoreCase(sortBy)) {
            System.out.println("WARN: L'ordinamento per provincia non supporta filtri aggiuntivi.");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "p.nome"));
            return clienteRepository.findAllSortedByProvinciaSedeLegale(pageable);
        }

        // Paginazione e ordinamento standard
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<Cliente> spec = ClienteSpecification.build(
                nome,
                fatturatoMin,
                dataInsAfter,
                dataUltContBefore
        );

        return clienteRepository.findAll(spec, pageable); // Modificato per paginazione
    }

    private Cliente mapToEntity(ClienteDTO payload) {
        Cliente cliente = new Cliente();
        cliente.setRagioneSociale(payload.ragioneSociale());
        cliente.setPartitaIva(payload.partitaIva());
        cliente.setDataInserimento(payload.dataInserimento());
        cliente.setDataUltimoContatto(payload.dataUltimoContatto());
        cliente.setFatturatoAnnuale(payload.fatturatoAnnuale());
        cliente.setPec(payload.pec());
        cliente.setTelefono(payload.telefono());
        cliente.setEmailContatto(payload.emailContatto());
        cliente.setNomeContatto(payload.nomeContatto());
        cliente.setTelefonoContatto(payload.telefonoContatto());
        cliente.setLogoAziendale(payload.logoAziendale());
        return cliente;
    }
}
