package com.piedrazul.personas.infrastructure.persistence.mapper;

import com.piedrazul.personas.domain.model.Especialidad;
import com.piedrazul.personas.infrastructure.persistence.entity.EspecialidadEntity;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadMapper {

    public EspecialidadEntity toEntity(Especialidad domain) {
        if (domain == null) return null;

        EspecialidadEntity entity = new EspecialidadEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        return entity;
    }

    public Especialidad toDomain(EspecialidadEntity entity) {
        if (entity == null) return null;

        Especialidad domain = new Especialidad();
        domain.setId(entity.getId());
        domain.setNombre(entity.getNombre());
        domain.setDescripcion(entity.getDescripcion());
        return domain;
    }
}