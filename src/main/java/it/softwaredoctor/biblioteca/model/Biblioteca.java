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
@Table(name = "biblioteca")
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column (name = "uuid")
    private UUID uuid;


    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Utente> utenti;

    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Libro> libri;

    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Prestito> prestiti;

    @Column(name = "nome")
    private String nome;

    @Column(name = "citta")
    private String citta;

    @PrePersist
    private void generateUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

}
