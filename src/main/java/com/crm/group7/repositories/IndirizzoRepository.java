package com.crm.group7.repositories;

import com.crm.group7.entities.Cliente;
import com.crm.group7.entities.Indirizzo;
import com.crm.group7.entities.enums.TipoIndirizzo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndirizzoRepository extends JpaRepository<Indirizzo, UUID> {
    Optional<Indirizzo> findByClienteAndTipoIndirizzo(Cliente cliente, TipoIndirizzo tipoIndirizzo);

    List<Indirizzo> findAllByCliente(Cliente cliente);

    List<Indirizzo> findAllByCap(int cap);

    List<Indirizzo> findAllByLocalita(String localita);

    Optional<Indirizzo> findByCivico(int civico);
    
    @Modifying
    @Transactional
    void deleteByCliente(Cliente cliente);
}