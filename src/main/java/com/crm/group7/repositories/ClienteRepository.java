package com.crm.group7.repositories;

import com.crm.group7.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    
    List<Cliente> findAllByFatturatoAnnualeGreaterThan(double fatturato);

    List<Cliente> findAllByRagioneSocialeContainingIgnoreCase(String parteNome);

    List<Cliente> findAllByDataInserimentoAfter(LocalDate dataInserimento);

    List<Cliente> findAllByDataUltimoContattoBefore(LocalDate dataUltimoContatto);
}
