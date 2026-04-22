package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.application.port.outgoing.MedicoSnapshotRepositoryPort;
import com.piedrazul.citas.domain.model.EstadoMedico;
import com.piedrazul.citas.domain.model.MedicoSnapshot;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import com.piedrazul.citas.infrastructure.persistence.entity.MedicoSnapshotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MedicoSnapshotRepositoryImpl implements MedicoSnapshotRepositoryPort {

    private final SpringDataMedicoSnapshotRepository repository;

    @Override
    public Optional<MedicoSnapshot> findById(MedicoId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public MedicoSnapshot save(MedicoSnapshot snapshot) {
        MedicoSnapshotEntity entity = toEntity(snapshot);
        MedicoSnapshotEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    private MedicoSnapshotEntity toEntity(MedicoSnapshot snapshot) {
        return MedicoSnapshotEntity.builder()
                .id(snapshot.getId().value())
                .nombreCompleto(snapshot.getNombreCompleto())
                .email(snapshot.getEmail())
                .especialidad(snapshot.getEspecialidad())
                .estado(snapshot.getEstado().name())
                .build();
    }

    private MedicoSnapshot toDomain(MedicoSnapshotEntity entity) {
        return new MedicoSnapshot(
                MedicoId.of(entity.getId()),
                entity.getNombreCompleto(),
                entity.getEmail(),
                entity.getEspecialidad(),
                EstadoMedico.valueOf(entity.getEstado())
        );
    }
}