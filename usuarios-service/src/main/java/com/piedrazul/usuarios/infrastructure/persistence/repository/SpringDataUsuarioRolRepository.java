package com.piedrazul.usuarios.infrastructure.persistence.repository;

import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioRolEntity;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUsuarioRolRepository extends JpaRepository<UsuarioRolEntity, UsuarioRolId> {
}
