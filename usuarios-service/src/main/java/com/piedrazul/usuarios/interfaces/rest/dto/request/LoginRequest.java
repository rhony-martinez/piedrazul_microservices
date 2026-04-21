package com.piedrazul.usuarios.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "username es obligatorio")
    private String username;

    @NotBlank(message = "password es obligatorio")
    private String password;
}
