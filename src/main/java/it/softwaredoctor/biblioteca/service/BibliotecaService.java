package it.softwaredoctor.biblioteca.service;

import it.softwaredoctor.biblioteca.model.Biblioteca;
import it.softwaredoctor.biblioteca.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;

    public UUID saveBiblioteca(Biblioteca biblioteca) {
        return bibliotecaRepository.save(biblioteca).getUuid();
    }
}
