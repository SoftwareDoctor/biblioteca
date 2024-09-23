package it.softwaredoctor.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "cod_libro")
    private String codLibro;

    @Column(name = "autore")
    private String autore;

    @Column(name = "titolo")
    private String titolo;

    @Column(name = "editore")
    private String editore;

    @Column(name = "anno_pubblicazione")
    private LocalDate annoPubblicazione;

    @Column(name = "quantita_disponibile")
    private Integer quantitaDisponibile;

    @Column(name = "genere")
    private String genere;

    @Column(name = "is_prestato")
    private Boolean isPrestato = Boolean.FALSE;

    @Column(name = "tot_prestiti")
    private Integer totPrestiti = 0;

    @ManyToOne
    @JoinColumn(name = "biblioteca_id", nullable = false)
    private Biblioteca biblioteca;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestito> prestiti;



    @PrePersist
    private void updateCodLibroCreateUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        String uuid = this.uuid.toString().replace("-", "");
        String uuidShort = uuid.substring(0, 7);

        if (titolo != null && !titolo.isEmpty()) {
            String firstCharUpper;
            if (titolo.length() >= 5) {
                firstCharUpper = titolo.substring(0, 5).toUpperCase();
            } else {
                firstCharUpper = titolo.toUpperCase();
            }
            String firstLetterUpper = titolo.substring(0, 1).toUpperCase();
            this.codLibro = String.format("%s-%s-%s", firstCharUpper, firstLetterUpper, uuidShort);
        } else {
            this.codLibro = uuidShort;
        }
    }

    public void prestareLibro() {
        if (quantitaDisponibile > 0) {
            System.out.println("Prestito effettuato per: " + this.titolo);
            this.isPrestato = true;
            this.totPrestiti++;
        } else {
            System.out.println("Nessuna copia disponibile per il prestito di: " + this.titolo);
        }
    }


    public void restituisciLibro() {
        if (isPrestato) {
            this.isPrestato = false;
        }
    }
}
