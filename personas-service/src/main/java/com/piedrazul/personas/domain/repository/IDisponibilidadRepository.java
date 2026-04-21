package com.piedrazul.personas.domain.repository;

public interface IDisponibilidadRepository {
    Disponibilidad save(Disponibilidad disponibilidad);
    Optional<Disponibilidad> findById(Long id);
    List<Disponibilidad> findAll();
}
