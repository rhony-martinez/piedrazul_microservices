package com.piedrazul.personas.infrastructure.persistence.repository;

import com.piedrazul.personas.domain.model.EstadoMedico;
import com.piedrazul.personas.domain.model.Medico;
import com.piedrazul.personas.domain.repository.IMedicoRepository;
import com.piedrazul.personas.infrastructure.persistence.entity.MedicoEntity;
import com.piedrazul.personas.infrastructure.persistence.entity.PersonaEntity;
import com.piedrazul.personas.infrastructure.persistence.mapper.MedicoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MedicoRepositoryImpl implements IMedicoRepository {

    private final SpringDataMedicoRepository springDataMedicoRepository;
    private final SpringDataPersonaRepository springDataPersonaRepository;
    private final MedicoMapper medicoMapper;

    @Override
    public Medico guardar(Medico medico) {
        PersonaEntity personaEntity = springDataPersonaRepository.findById(medico.getPersonaId())
                .orElseThrow(() -> new IllegalStateException(
                        "No existe PersonaEntity para personaId: " + medico.getPersonaId()
                ));

        MedicoEntity entity = new MedicoEntity();
        entity.setPersonaId(medico.getPersonaId());
        entity.setPersona(personaEntity);
        entity.setTipoProfesional(medico.getTipoProfesional());
        entity.setEstado(medico.getEstado());

        MedicoEntity saved = springDataMedicoRepository.save(entity);
        return medicoMapper.toDomain(saved);
    }

    @Override
    public Optional<Medico> buscarPorPersonaId(Long personaId) {
        return springDataMedicoRepository.findById(personaId)
                .map(medicoMapper::toDomain);
    }

    @Override
    public boolean existePorPersonaId(Long personaId) {
        return springDataMedicoRepository.existsByPersonaId(personaId);
    }

    @Override
    public List<Medico> listarTodos() {
        return springDataMedicoRepository.findAll()
                .stream()
                .map(medicoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Medico> listarActivos() {
        return springDataMedicoRepository.findByEstado(EstadoMedico.ACTIVO)
                .stream()
                .map(medicoMapper::toDomain)
                .toList();
    }
}