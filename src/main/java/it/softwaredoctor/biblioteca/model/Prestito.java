package it.softwaredoctor.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import static java.time.LocalDate.now;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "prestito")
public class Prestito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column (name = "uuid")
    private UUID uuid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @Column(name = "prestito_start_date")
    private LocalDate prestitoStartDate = now();

    @Column(name = "prestito_end_date")
    private LocalDate prestitoEndDate;

    @Column(name = "data_restituzione")
    private LocalDate dataRestituzione;

    @Column(name = "delay")
    private long delay;

    @ManyToOne
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;

    @PrePersist
    private void prepareForSave() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        this.prestitoEndDate = this.prestitoStartDate.plus(15, ChronoUnit.DAYS);
    }
}
