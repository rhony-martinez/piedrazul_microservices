package com.piedrazul.personas.domain.repository;

import com.piedrazul.personas.domain.model.Especialidad;
import java.util.List;
import java.util.Optional;

public interface IEspecialidadRepository {

    Especialidad guardar(Especialidad especialidad);

    Optional<Especialidad> buscarPorId(Long id);

    Optional<Especialidad> buscarPorNombre(String nombre);

    List<Especialidad> listarTodos();
}