package com.piedrazul.usuarios.infrastructure.persistence.mapper;

import com.piedrazul.usuarios.domain.model.EstadoUsuario;
import com.piedrazul.usuarios.domain.model.Rol;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioEntity;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioRolEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toDomain(UsuarioEntity entity) {
        Set<Rol> roles = entity.getRoles() == null
                ? new HashSet<>()
                : entity.getRoles().stream()
                .map(UsuarioRolEntity::getRol)
                .map(rolEntity -> Rol.builder()
                        .id(rolEntity.getId())
                        .nombre(rolEntity.getNombre())
                        .build())
                .collect(Collectors.toSet());

        return Usuario.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPassword())
                .estado(EstadoUsuario.valueOf(entity.getEstado()))
                .personaId(entity.getPersonaId())
                .intentosFallidos(entity.getIntentosFallidos())
                .roles(roles)
                .build();
    }

}
