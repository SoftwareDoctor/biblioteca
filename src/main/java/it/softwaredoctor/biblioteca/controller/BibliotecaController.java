package it.softwaredoctor.biblioteca.controller;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.service.BibliotecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/biblioteca")
@RestController
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    @PostMapping("/newBiblioteca")
    public ResponseEntity<Void> createBiblioteca(@RequestBody Biblioteca biblioteca) {
        bibliotecaService.saveBiblioteca(biblioteca);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
