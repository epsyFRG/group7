package com.crm.group7.repositories;

import com.crm.group7.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findAllByFatturatoAnnualeGreaterThan(double fatturato);

    List<Cliente> findAllByDataInserimentoAfter(LocalDate dataInserimento);

    List<Cliente> findAllByDataUltimoContattoBefore(LocalDate dataUltimoContatto);
}
