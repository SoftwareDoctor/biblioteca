package it.softwaredoctor.biblioteca.repository;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {
    Optional<Biblioteca> findByUuid(UUID uuid);

}

