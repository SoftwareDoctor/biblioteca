package it.softwaredoctor.biblioteca.controller;

import it.softwaredoctor.biblioteca.record.LibroToShow;
import it.softwaredoctor.biblioteca.record.LibroTosave;
import it.softwaredoctor.biblioteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/libro")
public class LibroController {

    private final LibroService libroService;

    @PostMapping("/newLibro/{uuidBiblioteca}")
    public ResponseEntity<Void> newLibro(@RequestBody LibroTosave libroRecord, @PathVariable UUID uuidBiblioteca) {
        try {
            UUID uuid = libroService.save(libroRecord, uuidBiblioteca);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{uuid}")
                    .buildAndExpand(uuid)
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<LibroToShow>> getLibro() {
        List<LibroToShow> libroToShowList = libroService.getAllLibroToShow();
        return ResponseEntity.ok(libroToShowList);
    }

    @GetMapping("/")
    public ResponseEntity<List<LibroToShow>> getLibroByFilters(@RequestParam(required = false) String titolo,
                                                               @RequestParam(required = false) String autore,
                                                               @RequestParam(required = false) String genere) {
        List<LibroToShow> libroToShowList = libroService.getAllLibroByFilters(titolo, autore, genere);
        return ResponseEntity.ok(libroToShowList);
    }

    @GetMapping("/disponibili/copie/tutte-biblioteche")
    public ResponseEntity<List<LibroToShow>> getBooksWith2CopiesAndDisponibileFromAllBiblioteche() {
        List<LibroToShow> libroToShowList = libroService.getBooksWith2CopiesAndDisponibileFromAllBiblioteche();
        return ResponseEntity.ok(libroToShowList);
    }

    @GetMapping("/allPrestiti")
    public ResponseEntity<List<LibroToShow>> getAllBorrowedBooks(@RequestParam UUID uuidBiblioteca) {
        List<LibroToShow> libroToShowList = libroService.getAllBorrowedBooks(uuidBiblioteca);
        return ResponseEntity.ok(libroToShowList);
    }
}
