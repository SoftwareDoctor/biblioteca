package it.softwaredoctor.biblioteca.repository;

import it.softwaredoctor.biblioteca.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByUuid(UUID uuidUtente);

    Optional<Utente> findByCodUtente(String codUtente);

    Optional<Utente> findByCodUtenteAndBibliotecaId(String codUtente, Long bibliotecaId);
}
