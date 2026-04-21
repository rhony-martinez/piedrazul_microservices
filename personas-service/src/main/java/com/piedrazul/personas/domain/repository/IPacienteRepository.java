package com.piedrazul.personas.domain.repository;

public interface IPacienteRepository {
    Paciente save(Paciente paciente);
    Optional<Paciente> findByPersonaId(Long personaId);
    Optional<Paciente> findByDni(String dni);
    boolean existsByPersonaId(Long personaId);
    List<Paciente> findAll();
}
