package it.softwaredoctor.biblioteca.service;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Libro;
import it.softwaredoctor.biblioteca.model.Prestito;
import it.softwaredoctor.biblioteca.model.Utente;
import it.softwaredoctor.biblioteca.record.PrestitoToShow;
import it.softwaredoctor.biblioteca.repository.BibliotecaRepository;
import it.softwaredoctor.biblioteca.repository.LibroRepository;
import it.softwaredoctor.biblioteca.repository.PrestitoRepository;
import it.softwaredoctor.biblioteca.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrestitoService {

    private final PrestitoRepository prestitoRepository;
    private final LibroRepository libroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final LibroService libroService;
    private final UtenteRepository utenteRepository;

//    @Transactional
//    public List<Biblioteca> createPrestito(UUID uuidBiblioteca, String codiceUtente, String codiceLibro) {
//        Biblioteca biblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca)
//                .orElseThrow(() -> new NoSuchElementException("La biblioteca con codice " + uuidBiblioteca + " non esiste."));
//        bibliotecaRepository.findByUuid(uuidBiblioteca).ifPresent(bib -> log.info("Biblioteca trovata: " + bib));
//
//        Utente utente = biblioteca.getUtenti().stream()
//                .filter(u -> u.getCodUtente().equals(codiceUtente))
//                .findFirst()
//                .orElseThrow(() -> new NoSuchElementException("L'utente con codice " + codiceUtente + " non esiste."));
//        utenteRepository.findByUuid(utente.getUuid()).ifPresent(ut -> log.info("utente trovato: " + ut));
//
//        Libro libro = biblioteca.getLibri().stream()
//                .filter(l -> l.getCodLibro().equals(codiceLibro))
//                .findFirst()
//                .orElseThrow(() -> new NoSuchElementException("Il libro con codice " + codiceLibro + " non esiste."));
//
//        libroRepository.findByUuid(libro.getUuid()).ifPresent(lib -> log.info("libro trovato: " + lib));
//
//
//        // Verificare se il libro è disponibile in questa biblioteca
//        if (libro.getQuantitaDisponibile() == 0) {
//            // Cercare altre biblioteche che hanno il libro disponibile
//            List<Biblioteca> librariesWithBookAvailable = bibliotecaRepository.findAll().stream()
//                    .filter(b -> b.getLibri().stream()
//                            .anyMatch(l -> l.getCodLibro().equals(codiceLibro) && l.getQuantitaDisponibile() > 0))
//                    .collect(Collectors.toList());
//
//            // Restituire una lista di biblioteche con copie disponibili
//            return librariesWithBookAvailable;
//        }
//
//        // Controllare il numero di prestiti attivi dell'utente in tutte le biblioteche
//        int totPrestitiUtente = (utente.getTotPrestitiUtente() != null) ? utente.getTotPrestitiUtente() : 0;
//        if (totPrestitiUtente <= 5) {
//            // Creare il nuovo prestito
//            Prestito prestito = new Prestito();
//            prestito.setUtente(utente);
//            prestito.setLibro(libro);
//            prestitoRepository.save(prestito);
//            // Aggiornare la quantità disponibile del libro
//            libro.setQuantitaDisponibile(libro.getQuantitaDisponibile() - 1);
//            libro.prestareLibro();
//            libro.setBiblioteca(biblioteca);
//            utente.setTotPrestitiUtente(totPrestitiUtente + 1);
//            utenteRepository.save(utente);
//            bibliotecaRepository.save(biblioteca);
//
//            log.info("Prestito salvato nel database.");
//        } else {
//            throw new IllegalStateException("L'utente ha già raggiunto il limite massimo di 5 prestiti attivi.");
//        }
//        return Collections.emptyList(); // Nessuna biblioteca alternativa se il prestito è stato creato
//    }


    // calcolare alla consegna del libro

    private void calculateDelay(Prestito prestito) {
        long delay;
        if (prestito.getPrestitoEndDate().isBefore(prestito.getDataRestituzione())) {
            delay = java.time.temporal.ChronoUnit.DAYS.between(prestito.getPrestitoEndDate(), prestito.getDataRestituzione());
        } else {
            delay = 0;
        }
    }
    // trovare prestiti scaduti

    public List<Prestito> findExpiredPrestiti() {
        LocalDate currentDate = LocalDate.now();
        return prestitoRepository.findAll().stream()
                .filter(p -> p.getPrestitoEndDate().isBefore(currentDate))
                .collect(Collectors.toList());
    }

    public void restituireLibro(String codUtente, String codLibro) {
        Optional<Utente> utenteOptional = utenteRepository.findByCodUtente(codUtente);
        if (!utenteOptional.isPresent()) {
            throw new IllegalArgumentException("Utente non trovato per il codice fornito.");
        }
        Utente utente = utenteOptional.get();
        Optional<Libro> libroOptional = libroRepository.findLibroByCodLibro(codLibro);

        if (!libroOptional.isPresent()) {
            throw new IllegalArgumentException("Libro non trovato per il codice fornito.");
        }
        Libro libro = libroOptional.get();
        Optional<Prestito> prestitoOptional = prestitoRepository.findPrestitoByUtenteAndLibro(utente, libro);

        if (!prestitoOptional.isPresent()) {
            throw new IllegalArgumentException("Prestito non trovato per l'utente e il libro forniti.");
        }
        Prestito prestito = prestitoOptional.get();
        libro.setQuantitaDisponibile(libro.getQuantitaDisponibile() + 1);
        libro.restituisciLibro();
        prestito.setDataRestituzione(LocalDate.now());
        calculateDelay(prestito);
        prestitoRepository.save(prestito);
        libroRepository.save(libro);
        utente.setTotPrestitiUtente(utente.getTotPrestitiUtente() - 1);
        utenteRepository.save(utente);
    }


    public List<PrestitoToShow> getAllPrestitiByUuidBiblioteca(UUID uuidBiblioteca) {
        Biblioteca biblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca)
                .orElseThrow(() -> new NoSuchElementException("Nessuna biblioteca trovata: " + uuidBiblioteca));
        return prestitoRepository.findAllByBiblioteca(biblioteca).stream()
                .map(PrestitoToShow::fromPrestito)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> createPrestito(UUID uuidBiblioteca, String codiceUtente, String codiceLibro) {
        Biblioteca biblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca)
                .orElseThrow(() -> new NoSuchElementException("La biblioteca con codice " + uuidBiblioteca + " non esiste."));
        log.info("Biblioteca trovata: {}", biblioteca);

        // Tentativo di trovare l'utente registrato
        Utente utente = null;
        if (codiceUtente != null) {
            utente = utenteRepository.findByCodUtenteAndBibliotecaId(codiceUtente, biblioteca.getId())
                    .orElse(null); // Non lanciare un'eccezione se l'utente non esiste
            if (utente != null) {
                log.info("Utente trovato: {}", utente);
            } else {
                log.warn("L'utente con codice {} non esiste nella biblioteca.", codiceUtente);
            }
        }

        // Tentare di trovare il libro nella biblioteca specificata
        Optional<Libro> libroOptional = libroRepository.findLibroByCodLibroAndBibliotecaUuid(codiceLibro, biblioteca.getUuid());

        if (!libroOptional.isPresent()) {
            // Se il libro non è presente nella biblioteca attuale, controlla le altre biblioteche
            List<Biblioteca> bibliotecheConLibroDisponibile = bibliotecaRepository.findAll().stream()
                    .filter(b -> b.getLibri().stream()
                            .anyMatch(l -> l.getCodLibro().equals(codiceLibro) && l.getQuantitaDisponibile() > 0))
                    .collect(Collectors.toList());

            if (!bibliotecheConLibroDisponibile.isEmpty()) {
                // Restituisci la lista delle biblioteche che hanno il libro disponibile
                return bibliotecheConLibroDisponibile.stream()
                        .map(b -> b.getNome() + " " + b.getCitta())
                        .collect(Collectors.toList());
            } else {
                throw new NoSuchElementException("Il libro con codice " + codiceLibro + " non è disponibile in nessuna biblioteca.");
            }
        }

        // Se il libro è trovato nella biblioteca attuale, verifica la disponibilità
        Libro libro = libroOptional.get();
        if (libro.getQuantitaDisponibile() <= 0) {
            throw new NoSuchElementException("Il libro con codice " + codiceLibro + " non è disponibile nella biblioteca.");
        }

        // Creare il nuovo prestito solo se l'utente è registrato
        if (utente != null) {
            Prestito prestito = new Prestito();
            prestito.setUtente(utente);
            prestito.setLibro(libro);
            prestito.setBiblioteca(biblioteca);
            prestitoRepository.save(prestito);

            // Aggiornare la quantità disponibile del libro
            libro.setQuantitaDisponibile(libro.getQuantitaDisponibile() - 1);
            libro.prestareLibro();
            libroRepository.save(libro);

            // Aggiornare il totale dei prestiti dell'utente
            utente.setTotPrestitiUtente(utente.getTotPrestitiUtente() + 1);
            utenteRepository.save(utente);

            log.info("Prestito salvato nel database.");
        } else {
            // Utente non registrato, ritornare la lista delle biblioteche con il libro disponibile
            log.warn("L'utente non è registrato. Mostrando le biblioteche con il libro disponibile.");
            return bibliotecaRepository.findAll().stream()
                    .filter(b -> b.getLibri().stream()
                            .anyMatch(l -> l.getCodLibro().equals(codiceLibro) && l.getQuantitaDisponibile() > 0))
                    .map(b -> b.getNome() + " " + b.getCitta())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList(); // Nessuna biblioteca alternativa se il prestito è stato creato
    }
}
