//package com.crm.group7.controllers;
//
//import com.crm.group7.entities.Utente;
//import com.crm.group7.exceptions.ValidationException;
//import com.crm.group7.payloads.UtenteDTO;
//import com.crm.group7.service.UtenteService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Locale;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/utente")
//public class UtenteController {
//    @Autowired
//    private UtenteService utenteService;
//
//
//    @GetMapping
//
//    public Page<Utente> findAll(@RequestParam(defaultValue = "0") int page,
//                                @RequestParam(defaultValue = "10") int size,
//                                @RequestParam(defaultValue = "id") String sortBy
//                                ){
//        return this.utenteService.findAll(page,size,sortBy);
//    }
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Utente createUtente(@RequestBody @Validated UtenteDTO payload , BindingResult validationResult){
//        if(validationResult.hasErrors()){
//            throw new ValidationException(validationResult.getFieldErrors()
//                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
//        }
//        return this.utenteService.save(payload);
//    }
//
//    @GetMapping("/me")
//    public Utente getProfile(@AuthenticationPrincipal Utente currentAuthenticateUtente){
//        return currentAuthenticateUtente;
//    }
//@PutMapping({"/idUtente"})
//    public Utente findByIdAndUpdate(@PathVariable UUID idUtente, @RequestBody UtenteDTO payload){
//        return this.utenteService.findByAndUpdate(idUtente,payload);
//}
// @DeleteMapping({"/idUtente"})
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//
//    public void findByAndDelete (@PathVariable UUID idUtente){
//        this.utenteService.findIdAndDelete(idUtente);
// }
//  @PatchMapping({"/idutente/avtar"})
//    public String uploadImage(@RequestParam("avatar")MultipartFile file, Locale locale)
//      throws IOException{
//        System.out.println(locale);
//
//        System.out.println(file.getSize());
//        System.out.println(file.getOriginalFilename());
//        return  this.utenteService.uploadAvatar(file);
//  }
//
//
//}
