package com.piedrazul.personas.domain.repository;

public interface IPersonaRepository {
    Persona save(Persona persona);
    Persona update(Persona persona);
    Optional<Persona> findById(Long id);
    Optional<Persona> findByDni(String dni);
    boolean existsByDni(String dni);
    List<Persona> findAll();
}
