package com.piedrazul.usuarios.domain.repository;

import com.piedrazul.usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Integer id);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorPersonaId(Integer personaId);

    boolean existePorUsername(String username);

    boolean existePorPersonaId(Integer personaId);

    List<Usuario> listarTodos();
}
