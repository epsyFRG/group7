//package com.crm.group7.controller;
//
//import com.crm.group7.entities.Cliente;
//import com.crm.group7.payloads.ClienteDTO;
//import com.crm.group7.service.ClienteService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/cliente")
//public class ClienteController {
//    @Autowired
//    private ClienteService clienteService;
//
//    @GetMapping
//    public List<Cliente> filtroClienti(
//            @RequestParam(required = false) UUID id,
//            @RequestParam(required = false) String nome,
//            @RequestParam(required = false) String email) {
//        //in caso filtra per ID
//        if (id != null) {
//            Cliente cliente = clienteService.findClienteById();
//            return cliente != null ? Collections.singletonList(cliente) : Collections.emptyList();
//        }
//        //in caso filtra per Nome
//        else if (nome != null && !nome.isEmpty()) {
//            return clienteService.findAllSortedByNome();
//        }
//
//
//        //In caso di Default restituisce tutti
//        else {
//            return clienteService.findAllSortedByNome();
//        }
//
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Cliente createUser(@RequestBody ClienteDTO payload) {
//        return this.clienteService.saveCliente(payload);
//    }
//
//
//}
