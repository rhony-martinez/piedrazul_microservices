package com.piedrazul.usuarios.interfaces.rest.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private Integer id;
    private Integer personaId;
    private String username;
    private String estado;
    private int intentosFallidos;
    private Set<String> roles;
}
