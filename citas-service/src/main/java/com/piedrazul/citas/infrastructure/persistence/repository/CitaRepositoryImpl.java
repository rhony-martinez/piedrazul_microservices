package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.application.port.outgoing.CitaRepositoryPort;
import com.piedrazul.citas.domain.model.Cita;
import com.piedrazul.citas.domain.valueobjects.CitaId;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import com.piedrazul.citas.infrastructure.persistence.entity.CitaEntity;
import com.piedrazul.citas.infrastructure.persistence.mapper.CitaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CitaRepositoryImpl implements CitaRepositoryPort {

    private final SpringDataCitaRepository springDataCitaRepository;
    private final CitaPersistenceMapper mapper;

    @Override
    public Cita save(Cita cita) {
        CitaEntity entity = mapper.toEntity(cita);
        CitaEntity savedEntity = springDataCitaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Cita> findById(CitaId id) {
        return springDataCitaRepository.findById(id.toString())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByMedicoIdAndFechaHora(MedicoId medicoId, LocalDateTime fechaHora) {
        return springDataCitaRepository.existsByMedicoIdAndFechaHora(medicoId.value(), fechaHora);
    }

    @Override
    public List<LocalDateTime> findFechasOcupadasPorMedico(
            MedicoId medicoId,
            LocalDateTime desde,
            LocalDateTime hasta) {

        return springDataCitaRepository.findFechasOcupadas(
                medicoId.value(),
                desde,
                hasta
        );
    }
}