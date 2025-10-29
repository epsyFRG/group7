package com.crm.group7.controller;

import com.crm.group7.entities.Cliente;
import com.crm.group7.entities.Comune;
import com.crm.group7.entities.Indirizzo;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.payloads.IndirizzoDTO;
import com.crm.group7.repositories.ClienteRepository;
import com.crm.group7.repositories.ComuneRepository;
import com.crm.group7.service.IndirizzoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/indirizzi")
public class IndirizzoController {

    @Autowired
    private IndirizzoService indirizzoService;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ComuneRepository comuneRepository;

    // POST http://localhost:3001/indirizzi?clienteId={uuid}&comuneId={uuid}
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Indirizzo createIndirizzo(@RequestParam("clienteId") UUID clienteId, @RequestParam("comuneId") UUID comuneId, @Valid @RequestBody IndirizzoDTO body) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new NotFoundException(clienteId));
        Comune comune = comuneRepository.findById(comuneId).orElseThrow(() -> new NotFoundException(comuneId));

        Indirizzo toSave = Indirizzo.builder()
                .via(body.via())
                .civico(body.civico())
                .localita(body.localita())
                .cap(body.cap())
                .tipoIndirizzo(body.tipoIndirizzo())
                .cliente(cliente)
                .comune(comune)
                .build();

        return indirizzoService.saveIndirizzo(toSave);
    }

    // GET http://localhost:3001/indirizzi?page=0&size=20&sort=via
    @GetMapping
    public Page<Indirizzo> getIndirizzi(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort) {
        return indirizzoService.getIndirizzi(page, size, sort);
    }

    // GET http://localhost:3001/indirizzi/{id}
    @GetMapping("/{id}")
    public Indirizzo getIndirizzoById(@PathVariable UUID id) {
        return indirizzoService.findIndirizzoById(id);
    }

    // PUT http://localhost:3001/indirizzi/{id}
    @PutMapping("/{id}")
    public Indirizzo updateIndirizzo(@PathVariable UUID id, @Valid @RequestBody IndirizzoDTO body) {
        Indirizzo payload = Indirizzo.builder()
                .via(body.via())
                .civico(body.civico())
                .localita(body.localita())
                .cap(body.cap())
                .tipoIndirizzo(body.tipoIndirizzo())
                .build();

        return indirizzoService.findIndirizzoAndUpdate(id, payload);
    }

    // DELETE http://localhost:3001/indirizzi/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIndirizzo(@PathVariable UUID id) {
        indirizzoService.findIndirizzoAndDelete(id);
    }
}
