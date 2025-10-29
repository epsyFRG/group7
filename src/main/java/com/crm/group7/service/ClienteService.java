package com.crm.group7.service;

import com.crm.group7.entities.Cliente;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.payloads.ClienteDTO;
import com.crm.group7.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    // CREATE
    public Cliente saveCliente(ClienteDTO dto) {
        Cliente cliente = mapToEntity(dto);
        return clienteRepository.save(cliente);
    }

    // READ
    public List<Cliente> getClienti() {
        return clienteRepository.findAll();
    }

    public Cliente findClienteById(UUID clienteId) {
        return clienteRepository.findById(clienteId).orElseThrow(() -> new NotFoundException(clienteId));
    }

    // UPDATE
    public Cliente findClienteAndUpdate(UUID clienteId, ClienteDTO payload) {
        Cliente found = findClienteById(clienteId);

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
        found.setLogoAziendale(payload.logoAziendale());

        return clienteRepository.save(found);
    }

    // DELETE
    public void findClienteAndDelete(UUID clienteId) {
        Cliente found = findClienteById(clienteId);
        clienteRepository.delete(found);
    }

    // ordinamento clienti per nome
    public List<Cliente> findAllSortedByNome() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "nomeContatto"));
    }

    // ordinamento clienti per fatturato annuale
    public List<Cliente> findAllSortedByFatturatoAnnuale() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "fatturatoAnnuale"));
    }

    // ordinamento clienti per data di inserimento
    public List<Cliente> findAllSortedByDataInserimento() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "dataInserimento"));
    }

    // ordinamento clienti per data ultimo contatto
    public List<Cliente> findAllSortedByDataUltimoContatto() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "dataUltimoContatto"));
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
