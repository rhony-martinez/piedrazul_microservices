package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.application.port.outgoing.PacienteSnapshotRepositoryPort;
import com.piedrazul.citas.domain.model.PacienteSnapshot;
import com.piedrazul.citas.domain.valueobjects.PacienteId;
import com.piedrazul.citas.infrastructure.persistence.entity.PacienteSnapshotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PacienteSnapshotRepositoryImpl implements PacienteSnapshotRepositoryPort {

    private final SpringDataPacienteSnapshotRepository repository;

    @Override
    public Optional<PacienteSnapshot> findById(PacienteId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public PacienteSnapshot save(PacienteSnapshot snapshot) {
        PacienteSnapshotEntity entity = toEntity(snapshot);
        PacienteSnapshotEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    private PacienteSnapshotEntity toEntity(PacienteSnapshot snapshot) {
        return PacienteSnapshotEntity.builder()
                .id(snapshot.getId().value())
                .nombreCompleto(snapshot.getNombreCompleto())
                .email(snapshot.getEmail())
                .telefono(snapshot.getTelefono())
                .activo(snapshot.isActivo())
                .build();
    }

    private PacienteSnapshot toDomain(PacienteSnapshotEntity entity) {
        return new PacienteSnapshot(
                PacienteId.of(entity.getId()),
                entity.getNombreCompleto(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.isActivo()
        );
    }
}