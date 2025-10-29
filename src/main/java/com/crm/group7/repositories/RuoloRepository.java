package com.crm.group7.repositories;

import com.crm.group7.entities.Ruolo;
import com.crm.group7.entities.enums.Ruoli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, UUID> {
    Optional<Ruolo> findByRuolo(Ruoli ruolo);
}
