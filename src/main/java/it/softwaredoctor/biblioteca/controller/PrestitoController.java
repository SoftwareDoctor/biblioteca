package it.softwaredoctor.biblioteca.controller;

import it.softwaredoctor.biblioteca.service.PrestitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prestito")
public class PrestitoController {

    private final PrestitoService prestitoService;

    @PostMapping("/newPrestito")
    public ResponseEntity<List<String>> createPrestito(@RequestParam UUID uuidBiblioteca, @RequestParam String codiceUtente, @RequestParam String codiceLibro) {
        List<String> librariesWithBookAvailable = prestitoService.createPrestito(uuidBiblioteca, codiceUtente, codiceLibro);
        if (librariesWithBookAvailable.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(librariesWithBookAvailable);
        }
    }

    @PutMapping("/")
    public ResponseEntity<Void> restituireLibro(@RequestParam String codiceUtente, @RequestParam String codiceLibro) {
        prestitoService.restituireLibro(codiceUtente, codiceLibro);
        return ResponseEntity.noContent().build();
    }
}
