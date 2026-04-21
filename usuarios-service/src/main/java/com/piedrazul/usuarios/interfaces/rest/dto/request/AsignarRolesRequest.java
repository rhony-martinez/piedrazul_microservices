package com.piedrazul.usuarios.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarRolesRequest {

    @NotEmpty(message = "Debe enviar al menos un rol")
    private List<String> roles;
}
