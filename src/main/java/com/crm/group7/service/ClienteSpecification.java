package com.crm.group7.service;

import com.crm.group7.entities.Cliente;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClienteSpecification {

    // Specifica per "Parte del nome"
    public static Specification<Cliente> nomeContains(String partialName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nomeContatto")), "%" + partialName.toLowerCase() + "%");
    }

    // Fatturato annuale
    public static Specification<Cliente> fatturatoMin(Double minFatturato) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("fatturatoAnnuale"), minFatturato);
    }

    // Data inserimento
    public static Specification<Cliente> dataInserimentoAfter(LocalDate data) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("dataInserimento"), data);
    }

    // Data ultimo contatto
    public static Specification<Cliente> dataUltimoContattoBefore(LocalDate data) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("dataUltimoContatto"), data);
    }

    // Per costruire la query finale
    public static Specification<Cliente> build(
            String nome,
            Double fatturatoMin,
            LocalDate dataInsAfter,
            LocalDate dataUltContBefore
    ) {
        // Trova tutto
        Specification<Cliente> spec = Specification.where(null);

        // Filtri
        if (nome != null && !nome.isEmpty()) {
            spec = spec.and(nomeContains(nome));
        }
        if (fatturatoMin != null) {
            spec = spec.and(fatturatoMin(fatturatoMin));
        }
        if (dataInsAfter != null) {
            spec = spec.and(dataInserimentoAfter(dataInsAfter));
        }
        if (dataUltContBefore != null) {
            spec = spec.and(dataUltimoContattoBefore(dataUltContBefore));
        }

        return spec;
    }
}