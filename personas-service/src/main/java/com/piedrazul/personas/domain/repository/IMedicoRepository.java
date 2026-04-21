package com.piedrazul.personas.domain.repository;

public interface IMedicoRepository {
    Medico save(Medico medico);
    Medico update(Medico medico);
    Optional<Medico> findByPersonaId(Long personaId);
    boolean existsByPersonaId(Long personaId);
    List<Medico> findAll();
    List<Medico> findAllActivos();
}
