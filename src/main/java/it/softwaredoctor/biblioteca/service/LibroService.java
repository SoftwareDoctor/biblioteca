package it.softwaredoctor.biblioteca.service;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.model.Libro;
import it.softwaredoctor.biblioteca.model.Prestito;
import it.softwaredoctor.biblioteca.model.Utente;
import it.softwaredoctor.biblioteca.record.LibroToShow;
import it.softwaredoctor.biblioteca.record.LibroTosave;
import it.softwaredoctor.biblioteca.repository.BibliotecaRepository;
import it.softwaredoctor.biblioteca.repository.LibroRepository;
import it.softwaredoctor.biblioteca.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final UtenteRepository utenteRepository;

    public UUID save(LibroTosave libroRecord, UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        List<Libro> libri = biblioteca.getLibri();
        Libro libro = fromLibroToSaveToLibro(libroRecord);

        List<Libro> existingLibri = libroRepository.findByTitolo(libro.getTitolo());

        Optional<Libro> existingLibroInBiblioteca = existingLibri.stream()
                .filter(l -> l.getBiblioteca().getUuid().equals(biblioteca.getUuid()))
                .findFirst();

        if (existingLibroInBiblioteca.isPresent()) {
            Libro libroEsistente = existingLibroInBiblioteca.get();
            libroEsistente.setQuantitaDisponibile(libroEsistente.getQuantitaDisponibile() + 1);
            libroRepository.save(libroEsistente);
            return libroEsistente.getUuid();
        } else {
            libro.setBiblioteca(biblioteca);
            libro.setQuantitaDisponibile(1);
            libroRepository.save(libro);
            libri.add(libro);
            biblioteca.setLibri(libri);
            bibliotecaRepository.save(biblioteca);
            return libro.getUuid();
        }
    }

    //ricerca in tutte le biblioteche
    public List<LibroToShow> getAllLibroToShow() {
        return libroRepository.findAll()
                .stream()
                .map(LibroToShow::fromLibro)
                .distinct()
                .collect(Collectors.toList());
    }

    //ricerca in una precisa biblioteca
    public List<LibroToShow> getDistinctBooksForBiblioteca(UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
                .map(LibroToShow::fromLibro)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<LibroToShow> getAllLibroByFilters(String titolo, String autore, String genere) {
        return libroRepository.findAll()
                .stream()
                .filter(libro -> (titolo == null || libro.getTitolo().toLowerCase().contains(titolo.toLowerCase())) &&
                        (autore == null || libro.getAutore().toLowerCase().contains(autore.toLowerCase())) &&
                        (genere == null || libro.getGenere().toLowerCase().contains(genere.toLowerCase())))
                .map(LibroToShow::fromLibro)
                .distinct()
                .collect(Collectors.toList());
    }

    private Libro fromLibroToSaveToLibro(LibroTosave libroToSave) {
        return Libro.builder()
                .autore(libroToSave.autore())
                .editore(libroToSave.editore())
                .annoPubblicazione(libroToSave.annoPubblicazione())
                .genere(libroToSave.genere())
                .titolo(libroToSave.titolo())
                .build();
    }

    public List<LibroToShow> getBooksWith2CopiesAndDisponibileFromAllBiblioteche() {
        List<Biblioteca> biblioteche = bibliotecaRepository.findAll();
        List<LibroToShow> libri = biblioteche.stream()
                .flatMap(biblioteca -> biblioteca.getLibri().stream()
                        .filter(libro -> libro.getQuantitaDisponibile() >= 2)
                        .map(LibroToShow::fromLibro)
                )
                .collect(Collectors.toList());
        return libri;
    }

    // restituire i libri con quantità disponibile maggiore di zero. Utilizziamo un Predicate per filtrare e map per restituire solo i titoli.
    public List<String> getTitlesOfAvailableBooks() {
        List<String> libri = libroRepository.findAll().stream()
                .filter(l -> l.getQuantitaDisponibile() > 0)
                .map(Libro::getTitolo)
                .collect(Collectors.toList());
        return libri;
    }

    //restituire i nomi dei libri prestati da una biblioteca
    public List<String> getTitlesOfBorrowedBooksByBank(UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getUtenti().stream()
                .flatMap(utente -> utente.getPrestiti().stream()) //unisco i prestiti di tutti gli utenti
                .map(prestito -> prestito.getLibro().getTitolo())
                .collect(Collectors.toList());
    }

    //restituire tutti i libri in prestito da una biblioteca
    public List<LibroToShow> getAllBorrowedBooks(UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getUtenti().stream()
                .flatMap(utente -> utente.getPrestiti().stream())
                .map(prestito -> LibroToShow.fromLibro(prestito.getLibro()))
                .collect(Collectors.toList());
    }

    //filtrare i libri per genere e anno di pubblicazione di tutte le biblioteche
    public List<LibroToShow> getFilteredBooks(String genere, LocalDate dataPubblicazione) {
        return libroRepository.findAll()
                .stream()
                .filter(libro -> libro.getGenere().equalsIgnoreCase(genere) && libro.getAnnoPubblicazione().equals(dataPubblicazione))
                .map(LibroToShow::fromLibro)
                .collect(Collectors.toList());
    }

    //Recuperare i Libri Prestati da un Utente Specifico e una biblioteca specifica
    public List<LibroToShow> getBorrowedBooksByUtente(UUID uuidUtente, UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        Optional<Utente> utenteOptional = utenteRepository.findByUuid(uuidUtente);
        Utente utente = utenteOptional.orElseThrow(() -> new NoSuchElementException("Utente con UUID " + uuidUtente + " non trovato")
        );
        List<Libro> libri = libroRepository.findByPrestiti_UtenteAndBiblioteca(utente, biblioteca);
        return libri.stream()
               .map(LibroToShow::fromLibro)
               .collect(Collectors.toList());
    }

    //Filtrare Libri per Data di Pubblicazione di una biblioteca
    public List<LibroToShow> getFilteredBooksByBiblioteca(UUID uuidBiblioteca, LocalDate dataPubblicazione) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
                .filter( b -> b.getAnnoPubblicazione().isEqual(dataPubblicazione))
                .map(LibroToShow::fromLibro)
                .collect(Collectors.toList());
    }

    //Ricerca di libri in base all'autore con Stream e map di una precisa biblioteca
    public List<LibroToShow> getFilteredBooksByBibliotecaAndAutore(UUID uuidBiblioteca, String autore) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
                .filter(b -> b.getAutore().equalsIgnoreCase(autore))
                .map(LibroToShow::fromLibro)
                .collect(Collectors.toList());
    }

    //Ricerca di libri in base alla presenza di una parola chiave nel titolo con Predicate e lambda di una precisa biblioteca
    public List<LibroToShow> getFilteredBooksByBibliotecaAndKeyword(UUID uuidBiblioteca, String keyword) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        Predicate<Libro> containsKeyword = libro -> libro.getTitolo().toLowerCase().contains(keyword.toLowerCase());
        return biblioteca.getLibri().stream()
                .filter(containsKeyword)
                .map(LibroToShow::fromLibro)
                .collect(Collectors.toList());
    }

    //Filtrare libri per genere e anno di pubblicazione con Predicate e lambda in tutte le biblioteche
    public List<LibroToShow> getFilteredBooksByGenereAndAnno(String genere, LocalDate dataPubblicazione) {
        return bibliotecaRepository.findAll()
                .stream()
                .flatMap(biblioteca -> biblioteca.getLibri().stream()
                        .filter(libro -> libro.getGenere().equalsIgnoreCase(genere) && libro.getAnnoPubblicazione().equals(dataPubblicazione))
                        .map(LibroToShow::fromLibro)
                )
                .collect(Collectors.toList());
    }

    //Trovare libri pubblicati tra due date con Predicate e lambda in tutte le biblioteche
    public List<LibroToShow> getFilteredBooksByDataPubblicazione(LocalDate dataMin, LocalDate dataMax) {
        return bibliotecaRepository.findAll()
                .stream()
                .flatMap(biblioteca -> biblioteca.getLibri().stream()
                        .filter(libro -> libro.getAnnoPubblicazione().isAfter(dataMin) && libro.getAnnoPubblicazione().isBefore(dataMax))
                        .map(LibroToShow::fromLibro)
                )
                .collect(Collectors.toList());
    }

    //Trovare libri pubblicati tra due date con Predicate e lambda in una precisa biblioteca
    public List<LibroToShow> getFilteredBooksByDataPubblicazioneAndBiblioteca(UUID uuidBiblioteca, LocalDate dataMin, LocalDate dataMax) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
                .filter(libro -> libro.getAnnoPubblicazione().isAfter(dataMin) && libro.getAnnoPubblicazione().isBefore(dataMax))
                .map(LibroToShow::fromLibro)
                .collect(Collectors.toList());
    }

    //Ricerca di libri con più copie disponibili tramite Stream, flatMap, e filter
    public Optional<Libro> getLibroConPiuCopieDisponibili(List<Biblioteca> biblioteche) {
        return biblioteche.stream()
                .flatMap(biblioteca -> biblioteca.getLibri().stream())
                .filter(libro -> libro.getQuantitaDisponibile() > 0)
                .max(Comparator.comparingInt(Libro::getQuantitaDisponibile));
    }

    //ricerca libri con il piu alto numero di prestiti
    public List<Libro> getAllLibriWithAltiPrestiti (UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
               .sorted(Comparator.comparingInt(libro -> libro.getPrestiti().size()))
               .collect(Collectors.toList());
    }

    //Filtrare libri non ancora restituiti con flatMap e filter
    public List<Libro> getAllLibroWithNoRestituzione (UUID uuidBiblioteca) {
        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findByUuid(uuidBiblioteca);
        Biblioteca biblioteca = optionalBiblioteca.orElseThrow(() ->
                new NoSuchElementException("Biblioteca con UUID " + uuidBiblioteca + " non trovata.")
        );
        return biblioteca.getLibri().stream()
               .flatMap(libro -> libro.getPrestiti().stream())
               .filter(prestito -> prestito.getDataRestituzione() == null)
               .map(Prestito::getLibro)
               .distinct()
               .collect(Collectors.toList());
    }
}









