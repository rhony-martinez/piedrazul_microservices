package com.piedrazul.personas.interfaces.rest.dto.request;

import com.piedrazul.personas.domain.model.TipoProfesional;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrarMedicoRequest {

    @NotNull
    private Long personaId;

    @NotNull
    private TipoProfesional tipoProfesional;
}