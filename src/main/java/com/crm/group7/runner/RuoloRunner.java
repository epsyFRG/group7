package com.crm.group7.runner;

import com.crm.group7.entities.Ruolo;
import com.crm.group7.entities.enums.Ruoli;
import com.crm.group7.repositories.RuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RuoloRunner implements CommandLineRunner {

    @Autowired
    private RuoloRepository ruoloRepository;

    @Override
    public void run(String... args) throws Exception {
        // Controlla e crea il ruolo USER se non esiste
        if (ruoloRepository.findByRuolo(Ruoli.UTENTE).isEmpty()) {
            Ruolo userRole = new Ruolo(Ruoli.UTENTE);
            ruoloRepository.save(userRole);
            System.out.println("Ruolo USER creato nel database.");
        }

        // Controlla e crea il ruolo ADMIN se non esiste
        if (ruoloRepository.findByRuolo(Ruoli.ADMIN).isEmpty()) {
            Ruolo adminRole = new Ruolo(Ruoli.ADMIN);
            ruoloRepository.save(adminRole);
            System.out.println("Ruolo ADMIN creato nel database.");
        }
    }
}