package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.port.incoming.ConsultarSlotsDisponiblesUseCase;
import com.piedrazul.citas.application.port.outgoing.CitaRepositoryPort;
import com.piedrazul.citas.application.port.outgoing.ConfiguracionRepositoryPort;
import com.piedrazul.citas.application.port.outgoing.DisponibilidadSnapshotRepositoryPort;
import com.piedrazul.citas.domain.model.DisponibilidadSnapshot;
import com.piedrazul.citas.domain.model.TimeRange;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConsultarSlotsDisponiblesService implements ConsultarSlotsDisponiblesUseCase {

    private final DisponibilidadSnapshotRepositoryPort disponibilidadRepo;
    private final CitaRepositoryPort citaRepo;
    private final ConfiguracionRepositoryPort configRepo;

    @Override
    public List<LocalDateTime> consultar(MedicoId medicoId) {

        DisponibilidadSnapshot snapshot = disponibilidadRepo.findByMedicoId(medicoId)
                .orElseThrow(() -> new RuntimeException("No hay disponibilidad"));

        int semanas = configRepo.obtener().getSemanasDisponibles();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fin = ahora.plusWeeks(semanas);

        List<LocalDateTime> citasExistentes =
                citaRepo.findFechasOcupadasPorMedico(medicoId, ahora, fin);

        Set<LocalDateTime> ocupados = new HashSet<>(citasExistentes);

        List<LocalDateTime> disponibles = new ArrayList<>();

        LocalDate fecha = ahora.toLocalDate();

        while (!fecha.isAfter(fin.toLocalDate())) {

            DayOfWeek dia = fecha.getDayOfWeek();

            List<TimeRange> rangos = snapshot.getHorariosSemanales().get(dia);

            if (rangos != null) {

                for (TimeRange rango : rangos) {

                    LocalTime hora = rango.getStart();

                    while (hora.isBefore(rango.getEnd())) {

                        LocalDateTime slot = LocalDateTime.of(fecha, hora);

                        if (slot.isAfter(ahora)
                                && !ocupados.contains(slot)
                                && !snapshot.getBloqueosEspecificos().contains(slot)) {

                            disponibles.add(slot);
                        }

                        hora = hora.plusMinutes(snapshot.getIntervaloMinutos());
                    }
                }
            }

            fecha = fecha.plusDays(1);
        }

        return disponibles;
    }
}