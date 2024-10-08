package it.softwaredoctor.biblioteca.repository;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Libro;
import it.softwaredoctor.biblioteca.model.Prestito;
import it.softwaredoctor.biblioteca.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrestitoRepository extends JpaRepository<Prestito, Long> {

//    Optional<Prestito> findPrestitoByUtenteAndLibro(Utente utente, Libro libro);

    Optional<Prestito> findPrestitoByUtenteUuidAndLibroUuid(UUID uuidUtente, UUID uuidLibro);

    List<Prestito> findAllByBiblioteca(Biblioteca biblioteca);


}
