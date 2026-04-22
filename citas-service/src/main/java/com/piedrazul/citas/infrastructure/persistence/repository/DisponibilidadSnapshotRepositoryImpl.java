package com.piedrazul.citas.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.citas.application.port.outgoing.DisponibilidadSnapshotRepositoryPort;
import com.piedrazul.citas.domain.model.DisponibilidadSnapshot;
import com.piedrazul.citas.domain.model.TimeRange;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import com.piedrazul.citas.infrastructure.persistence.entity.DisponibilidadSnapshotEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DisponibilidadSnapshotRepositoryImpl implements DisponibilidadSnapshotRepositoryPort {

    private final SpringDataDisponibilidadSnapshotRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<DisponibilidadSnapshot> findByMedicoId(MedicoId medicoId) {
        return repository.findById(medicoId.value())
                .map(this::toDomain);
    }

    @Override
    public DisponibilidadSnapshot save(DisponibilidadSnapshot snapshot) {
        DisponibilidadSnapshotEntity entity = toEntity(snapshot);
        DisponibilidadSnapshotEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    private DisponibilidadSnapshotEntity toEntity(DisponibilidadSnapshot snapshot) {
        Map<String, Object> horariosMap = new HashMap<>();
        snapshot.getHorariosSemanales().forEach((dia, rangos) -> {
            List<Map<String, String>> rangosList = rangos.stream()
                    .map(rango -> {
                        Map<String, String> rangoMap = new HashMap<>();
                        rangoMap.put("start", rango.getStart().toString());
                        rangoMap.put("end", rango.getEnd().toString());
                        return rangoMap;
                    })
                    .collect(Collectors.toList());
            horariosMap.put(dia.name(), rangosList);
        });

        return DisponibilidadSnapshotEntity.builder()
                .medicoId(snapshot.getMedicoId().value())
                .horariosSemanales(horariosMap)
                .bloqueosEspecificos(snapshot.getBloqueosEspecificos())
                .build();
    }

    private DisponibilidadSnapshot toDomain(DisponibilidadSnapshotEntity entity) {
        DisponibilidadSnapshot snapshot = new DisponibilidadSnapshot(MedicoId.of(entity.getMedicoId()));

        // Restaurar horarios semanales
        if (entity.getHorariosSemanales() != null) {
            entity.getHorariosSemanales().forEach((diaStr, rangosObj) -> {
                try {
                    DayOfWeek dia = DayOfWeek.valueOf(diaStr);
                    List<Map<String, String>> rangosList = (List<Map<String, String>>) rangosObj;
                    rangosList.forEach(rangoMap -> {
                        LocalTime start = LocalTime.parse(rangoMap.get("start"));
                        LocalTime end = LocalTime.parse(rangoMap.get("end"));
                        snapshot.agregarHorarioSemanal(dia, new TimeRange(start, end));
                    });
                } catch (Exception e) {
                    log.error("Error restaurando horarios semanales: {}", e.getMessage());
                }
            });
        }

        // Restaurar bloqueos específicos
        if (entity.getBloqueosEspecificos() != null) {
            entity.getBloqueosEspecificos().forEach(snapshot::agregarBloqueo);
        }

        return snapshot;
    }
}