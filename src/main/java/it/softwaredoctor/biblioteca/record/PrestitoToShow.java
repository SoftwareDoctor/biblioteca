package it.softwaredoctor.biblioteca.record;

import it.softwaredoctor.biblioteca.model.Prestito;
import java.time.LocalDate;

public record PrestitoToShow(

        String codLibro,
        LocalDate prestitoStartDate,
        LocalDate prestitoEndDate,
        LocalDate dataRestituzione,
        long delay) {

    public static PrestitoToShow fromPrestito(Prestito prestito) {
        return new PrestitoToShow(
                prestito.getLibro().getCodLibro(),
                prestito.getPrestitoStartDate(),
                prestito.getPrestitoEndDate(),
                prestito.getDataRestituzione(),
                prestito.getDelay());
    }
}
