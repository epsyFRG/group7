package com.crm.group7.service;

import com.crm.group7.entities.Ruolo;
import com.crm.group7.entities.Utente;
import com.crm.group7.entities.enums.Ruoli;
import com.crm.group7.exceptions.BadRequestException;
import com.crm.group7.exceptions.NotFoundException;
import com.crm.group7.payloads.UtenteDTO;
import com.crm.group7.repositories.RuoloRepository;
import com.crm.group7.repositories.UtenteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UtenteService {
    //    private static final long MAX_SIZE = 5 * 1024 * 1024;
//    private static final List<String> ALLOWED_TYPES = List.of("image/png", "image/jpeg");
//    private static  UUID id;
    @Autowired
    private UtenteRepository utenteRepository;

    //    @Autowired
//    private Cloudinary imageUploader;
    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private RuoloRepository ruoloRepository;

    //
//
//    public Page<Utente> findAll(int pageNumber, int pageSize, String sortBy){
//        if (pageSize > 50) pageSize=50;
//        Pageable pageable = PageRequest.of(pageNumber , pageSize , Sort.by(sortBy).ascending());
//        return this.utenteRepository.findAll(pageable);
//    }
    public Utente save(UtenteDTO payload) {
        this.utenteRepository.findByUsername(payload.username()).ifPresent(utente -> {
                    throw new BadRequestException("L'username " + utente.getUsername() + " è già in uso!");
                }
        );

        this.utenteRepository.findByEmail(payload.email()).ifPresent(utente -> {
            throw new BadRequestException("L'email '" + utente.getEmail() + "' è già registrata!");
        });

        Utente newUtente = new Utente(payload.username(), payload.email(), bcrypt.encode(payload.password()), payload.nome(), payload.cognome());
        newUtente.setAvatarURL("https://ui-avatars.com/api/?name=" + payload.nome());

        // 1. Inizializza la lista dei ruoli dell'utente (gestisce anche ruoli null)
        List<Ruolo> ruoliDaAssegnare = List.of();
        if (payload.ruoli() != null && !payload.ruoli().isEmpty()) {
            ruoliDaAssegnare = payload.ruoli().stream()
                    // 2. Per ogni ruolo (ADMIN, UTENTE), cercalo nel DB
                    .map(ruolo -> ruoloRepository.findByRuolo(ruolo)
                            .orElseThrow(() -> new RuntimeException("Ruolo '" + ruolo + "' non trovato nel database!")))
                    .toList();
        }

        if (ruoliDaAssegnare.isEmpty()) {
            Ruolo userRole = ruoloRepository.findByRuolo(Ruoli.UTENTE)
                    .orElseGet(() -> ruoloRepository.save(new Ruolo(Ruoli.UTENTE)));
            ruoliDaAssegnare = List.of(userRole);
        }

        newUtente.setRuoli(ruoliDaAssegnare);

        Utente savedUtente = this.utenteRepository.save(newUtente);

        log.info("L'utente con id: " + savedUtente.getId() + " è stato salvato correttamente con i ruoli: " + ruoliDaAssegnare);
        return savedUtente;
    }

    //
    public Utente findById(UUID idUtente) {
        return utenteRepository.findByIdWithRuoli(idUtente).orElseThrow(() -> new NotFoundException(idUtente));
    }

    //    public Utente findByAndUpdate(UUID idUtente , UtenteDTO payload){
//
//        Utente found= this.findById(idUtente);
//
//        if (!found.getEmail().equals(payload.email())){
//            this.utenteRepository.findByEmail(payload.email())
//                    .ifPresent(utente -> {
//                        throw  new BadRequestException("L'email" + utente.getEmail() + "è già in uso!");
//                    }
//                    );
//        }
//        found.setNome(payload.nome());
//        found.setCognome(payload.cognome());
//        found.setEmail(payload.email());
//        found.setPassword(payload.password());
//        found.setAvatarURL("https://ui-avatars.com/api/?name=" + payload.nome() + "+" + payload.cognome());
//        //Controlla e codifica la nuova password se fornita
//        if (payload.password()!=null && ! payload.password().isEmpty()){
//            found.setPassword(passwordEncoder.encode(payload.password()));
//        }
//        Utente modifiedUtente = this.utenteRepository.save(found);
//
//        log.info("L'untente con id" + modifiedUtente.getId() + "è stato modificato correttamente");
//
//        return modifiedUtente;
//    }
//
//    public void  findIdAndDelete(UUID idUtente){
//        Utente found = this.findById(idUtente);
//        this.utenteRepository.delete(found);
//    }
//
//    public  String uploadAvatar(MultipartFile file){
//        if (file.isEmpty()) throw  new BadRequestException("File vuoto");
//        if (file.getSize()> MAX_SIZE) throw  new BadRequestException("File troppo grande!");
//        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new BadRequestException("Formato non valido");
//
//        try{
//            Map reult =imageUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//            String imageURL=(String)  reult.get("secure_url");
//
//            return imageURL;
//        }catch (IOException exception){
//            throw new RuntimeException(exception);
//        }
//    }
    public Utente findByEmail(String email) {
        return this.utenteRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("L'utente con l'email '" + email + "' non è stato trovato"));

    }
}


