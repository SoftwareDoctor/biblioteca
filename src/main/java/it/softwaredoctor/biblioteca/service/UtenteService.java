package it.softwaredoctor.biblioteca.service;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Utente;
import it.softwaredoctor.biblioteca.record.UtenteToSave;
import it.softwaredoctor.biblioteca.repository.BibliotecaRepository;
import it.softwaredoctor.biblioteca.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final BibliotecaRepository bibliotecaRepository;

    public UUID createUser(UtenteToSave utente, UUID uuidBiblioteca) {
        Utente utenteToSave = fromUtenteToSaveToUtente(utente);
        Optional<Biblioteca> bibliotecaOptional = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = bibliotecaOptional.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        utenteToSave.setBiblioteca(biblioteca);
        return utenteRepository.save(utenteToSave).getUuid();
    }

    private Utente fromUtenteToSaveToUtente(UtenteToSave utenteToSave) {
        return Utente.builder()
                .nome(utenteToSave.nome())
                .cognome(utenteToSave.cognome())
                .email(utenteToSave.email())
                .password(utenteToSave.password())
                .build();
    }
}
