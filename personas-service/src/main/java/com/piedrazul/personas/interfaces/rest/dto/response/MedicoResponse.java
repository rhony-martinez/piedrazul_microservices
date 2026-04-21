package com.piedrazul.personas.interfaces.rest.dto.response;

import com.piedrazul.personas.domain.model.EstadoMedico;
import com.piedrazul.personas.domain.model.TipoProfesional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicoResponse {

    private Long personaId;
    private TipoProfesional tipoProfesional;
    private EstadoMedico estado;
}