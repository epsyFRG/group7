package com.crm.group7.repositories;

import com.crm.group7.entities.StatoFattura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatoFatturaRepository extends JpaRepository<StatoFattura, UUID> {

    Optional<StatoFattura> findByStato(String stato);

    boolean existsByStato(String stato);
}
