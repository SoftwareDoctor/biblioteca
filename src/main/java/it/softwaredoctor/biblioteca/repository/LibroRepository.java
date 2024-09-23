package it.softwaredoctor.biblioteca.repository;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Libro;
import it.softwaredoctor.biblioteca.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByTitolo(String titolo);

    List<Libro> findByPrestiti_UtenteAndBiblioteca(Utente utente, Biblioteca biblioteca);

    Optional<Libro> findLibroByCodLibro(String codLibro);

    Optional<Libro> findByUuid(UUID uuidLibro);

    Optional<Libro> findLibroByCodLibroAndBibliotecaUuid(String codiceLibro, UUID uuidBiblioteca);



}
