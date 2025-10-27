package com.crm.group7.repositories;

import com.crm.group7.entities.Utente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository  extends JpaRepository <Utente, UUID>  {

    Optional <Utente> findByEmail(String eamil);
}
