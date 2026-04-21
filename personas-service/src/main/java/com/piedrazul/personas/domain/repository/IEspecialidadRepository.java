package com.piedrazul.personas.domain.repository;

public interface IEspecialidadRepository {
    Especialidad save(Especialidad especialidad);
    Optional<Especialidad> findById(Long id);
    Optional<Especialidad> findByNombre(String nombre);
    List<Especialidad> findAll();
}
