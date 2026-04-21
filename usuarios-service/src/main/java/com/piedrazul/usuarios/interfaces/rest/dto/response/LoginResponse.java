package com.piedrazul.usuarios.interfaces.rest.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer id;
    private Integer personaId;
    private String username;
    private String estado;
    private Set<String> roles;
    private String message;
}
