package it.softwaredoctor.biblioteca.record;

import it.softwaredoctor.biblioteca.model.Libro;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record LibroToShow(String autore,
                          String titolo,
                          String editore,
                          LocalDate annoPubblicazione,
                          Integer quantitaDisponibile,
                          String genere,
                          String cittaBiblioteca,
                          String codLibro) {

    public static LibroToShow fromLibro(Libro libro) {
        return new LibroToShow(
                libro.getAutore(),
                libro.getTitolo(),
                libro.getEditore(),
                libro.getAnnoPubblicazione(),
                libro.getQuantitaDisponibile(),
                libro.getGenere(),
                libro.getBiblioteca() != null ? libro.getBiblioteca().getCitta() : null,
                libro.getCodLibro()
        );
    }
}
