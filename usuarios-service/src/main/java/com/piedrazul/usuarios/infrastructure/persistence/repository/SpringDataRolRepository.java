package com.piedrazul.usuarios.infrastructure.persistence.repository;

import com.piedrazul.usuarios.infrastructure.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataRolRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombre(String nombre);
}
