package com.piedrazul.usuarios.infrastructure.persistence.repository;

import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    @EntityGraph(attributePaths = {"roles", "roles.rol"})
    Optional<UsuarioEntity> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.rol"})
    Optional<UsuarioEntity> findByPersonaId(Integer personaId);

    boolean existsByUsername(String username);

    boolean existsByPersonaId(Integer personaId);

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.rol"})
    List<UsuarioEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.rol"})
    Optional<UsuarioEntity> findById(Integer id);
}
