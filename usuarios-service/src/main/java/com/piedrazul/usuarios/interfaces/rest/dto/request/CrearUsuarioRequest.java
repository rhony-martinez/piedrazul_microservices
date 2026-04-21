package com.piedrazul.usuarios.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearUsuarioRequest {

    @NotNull(message = "personaId es obligatorio")
    private Integer personaId;

    @NotBlank(message = "username es obligatorio")
    private String username;

    @NotBlank(message = "password es obligatorio")
    private String password;

    @NotEmpty(message = "Debe asignarse al menos un rol")
    private List<String> roles;
}
