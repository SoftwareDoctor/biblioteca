package it.softwaredoctor.biblioteca.controller;

import it.softwaredoctor.biblioteca.record.UtenteToSave;
import it.softwaredoctor.biblioteca.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/utente")
@RestController
public class UtenteController {

    private final UtenteService utenteService;

    @PostMapping("/newUtente")
    public ResponseEntity<Void> createUser(@RequestBody UtenteToSave userToSave, @RequestParam UUID uuidBiblioteca) {
        try {
            UUID uuid = utenteService.createUser(userToSave, uuidBiblioteca);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/api/v1/utente/{uuid}")
                    .buildAndExpand(uuid)
                    .toUri();
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
