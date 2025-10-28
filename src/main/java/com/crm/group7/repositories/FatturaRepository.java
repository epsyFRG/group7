package com.crm.group7.repositories;

import com.crm.group7.entities.Fattura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FatturaRepository extends JpaRepository<Fattura, UUID>, JpaSpecificationExecutor<Fattura> {

    boolean existsByNumero(Long numero);

    Page<Fattura> findAll(Specification<Fattura> spec, Pageable pageable);
}
