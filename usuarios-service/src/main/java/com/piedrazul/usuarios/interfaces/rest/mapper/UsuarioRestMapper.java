package com.piedrazul.usuarios.interfaces.rest.mapper;

import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.interfaces.rest.dto.response.LoginResponse;
import com.piedrazul.usuarios.interfaces.rest.dto.response.UsuarioResponse;

import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioRestMapper {

    private UsuarioRestMapper() {
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        Set<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toSet());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .personaId(usuario.getPersonaId())
                .username(usuario.getUsername())
                .estado(usuario.getEstado().name())
                .intentosFallidos(usuario.getIntentosFallidos())
                .roles(roles)
                .build();
    }

    public static LoginResponse toLoginResponse(Usuario usuario) {
        Set<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .id(usuario.getId())
                .personaId(usuario.getPersonaId())
                .username(usuario.getUsername())
                .estado(usuario.getEstado().name())
                .roles(roles)
                .message("Autenticación exitosa")
                .build();
    }
}
