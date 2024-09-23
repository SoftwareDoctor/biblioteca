package it.softwaredoctor.biblioteca.record;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record LibroTosave(
        String autore,
        String titolo,
        String editore,
        LocalDate annoPubblicazione,
        String genere) {
}
