package it.softwaredoctor.biblioteca.record;

import java.time.LocalDate;

public record LibroTosave(
        String autore,
        String titolo,
        String editore,
        LocalDate annoPubblicazione,
        String genere) {
}
