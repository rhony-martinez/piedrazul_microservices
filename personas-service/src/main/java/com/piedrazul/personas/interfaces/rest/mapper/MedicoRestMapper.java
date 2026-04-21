package com.piedrazul.personas.interfaces.rest.mapper;

import com.piedrazul.personas.domain.model.Medico;
import com.piedrazul.personas.interfaces.rest.dto.response.MedicoResponse;
import org.springframework.stereotype.Component;

@Component
public class MedicoRestMapper {

    public MedicoResponse toResponse(Medico medico) {
        return MedicoResponse.builder()
                .personaId(medico.getPersonaId())
                .tipoProfesional(medico.getTipoProfesional())
                .estado(medico.getEstado())
                .build();
    }
}