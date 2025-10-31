package com.crm.group7.service;

import com.crm.group7.entities.Cliente;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClienteSpecification {
    //    È un metodo statico, posso chiamarlo direttamente sulla classe che lo contiene
    //    (tipo ClienteSpecifications.nomeContains("Ziopera")) senza dover creare un'istanza per convenzione delle Spec.//
//    Diciamo che una Specification è un oggetto che definisce una parte di una clausola WHERE in una query JPA.
//    È parametrizzato con <Cliente>, quindi si applica a quell'Entità.

    //     Questo metodo restituisce una lambda. Ovvero l'implementazione del metodo toPredicate dell'interfaccia Specification.
//     I tre parametri sono i pezzi per costruire la query:
//
//    root: Rappresenta l'entità Cliente. Lo usi per accedere ai suoi attributi (le colonne del database).
//
//    query: Rappresenta la query JPA nel suo complesso (qui non viene usata, ma è necessaria per la firma).
//
//    criteriaBuilder: Questo è il costruttore. Lo usi per creare i pezzi effettivi della query, come LIKE, EQUAL, GREATER THAN, ecc.
//
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

    // Per costruire la query finale si usa il metodo build che mette insieme queste cose
    public static Specification<Cliente> build(
            String nome,
            Double fatturatoMin,
            LocalDate dataInsAfter,
            LocalDate dataUltContBefore
    ) {
        // Trova tutto
        Specification<Cliente> spec = Specification.where(null);

//        Questo è una sorta di costruttore condizionale sarebbe come dire: Se l'utente ha fornito un nome lo utilizzo.
        // Filtri
        if (nome != null && !nome.isEmpty()) {
            spec = spec.and(nomeContains(nome));
//          Qui prendio la specifica che ho costruito finora (all'inizio vuota) e la attacco in AND la nuova specifica nomeContains
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
//        Alla fine ritorno un singolo oggetto Specification che combina tutti i filtri richiesti
    }
}