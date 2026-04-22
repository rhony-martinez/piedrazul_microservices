package com.piedrazul.personas.infrastructure.persistence.repository;

import com.piedrazul.personas.domain.model.Especialidad;
import com.piedrazul.personas.domain.repository.IEspecialidadRepository;
import com.piedrazul.personas.infrastructure.persistence.entity.EspecialidadEntity;
import com.piedrazul.personas.infrastructure.persistence.mapper.EspecialidadMapper;
import com.piedrazul.personas.infrastructure.persistence.springdata.SpringDataEspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EspecialidadRepositoryImpl implements IEspecialidadRepository {

    private final SpringDataEspecialidadRepository springDataEspecialidadRepository;
    private final EspecialidadMapper mapper;

    @Override
    public Especialidad guardar(Especialidad especialidad) {
        EspecialidadEntity entity = mapper.toEntity(especialidad);
        EspecialidadEntity savedEntity = springDataEspecialidadRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Especialidad> buscarPorId(Long id) {
        return springDataEspecialidadRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Especialidad> buscarPorNombre(String nombre) {
        return springDataEspecialidadRepository.findByNombre(nombre)
                .map(mapper::toDomain);
    }

    @Override
    public List<Especialidad> listarTodos() {
        return springDataEspecialidadRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}