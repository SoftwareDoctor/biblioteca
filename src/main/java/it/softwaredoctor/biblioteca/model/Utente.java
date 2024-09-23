package it.softwaredoctor.biblioteca.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "cod_utente")
    private String codUtente;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "delay_total")
    private Integer delayTotal;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;

    @ToString.Exclude
    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Prestito> prestiti;

    @Column(name = "tot_prestiti_utente")
    private Integer totPrestitiUtente = 0;


        @PrePersist
    private void generateUUIDAndCodUtente() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        String uuid = this.uuid.toString().replace("-", "");
        String uuidShort = uuid.substring(0, 7);

        if (nome != null && !nome.isEmpty()) {
            String firstCharUpper;
            if (nome.length() >= 15) {
                firstCharUpper = nome.substring(0, 14).toUpperCase();
            } else {
                firstCharUpper = nome.toUpperCase();
            }
            String firstLetterUpper = nome.substring(0, 1).toUpperCase();
            this.codUtente = String.format("%s-%s-%s", firstCharUpper, firstLetterUpper, uuidShort);
        } else {
            this.codUtente = uuidShort;
        }
    }


}
