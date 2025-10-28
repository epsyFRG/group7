package com.crm.group7.repositories;

import com.crm.group7.entities.Comune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComuneRepository extends JpaRepository<Comune, UUID> {
}
