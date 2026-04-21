package com.piedrazul.usuarios.domain.repository;

import com.piedrazul.usuarios.domain.model.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolRepository {

    Optional<Rol> buscarPorId(Integer id);

    Optional<Rol> buscarPorNombre(String nombre);

    List<Rol> listarTodos();
}
