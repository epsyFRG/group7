package com.crm.group7.repositories;

import com.crm.group7.entities.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, UUID> {
    Optional<Provincia> findByNome(String nome);

    Optional<Provincia> findBySigla(String sigla);
}
