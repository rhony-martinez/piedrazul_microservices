package com.piedrazul.citas.application.port.outgoing;

import com.piedrazul.citas.domain.model.Cita;
import com.piedrazul.citas.domain.valueobjects.CitaId;
import com.piedrazul.citas.domain.valueobjects.MedicoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepositoryPort {
    Cita save(Cita cita);
    Optional<Cita> findById(CitaId id);
    boolean existsByMedicoIdAndFechaHora(com.piedrazul.citas.domain.valueobjects.MedicoId medicoId,
                                         java.time.LocalDateTime fechaHora);
    List<LocalDateTime> findFechasOcupadasPorMedico(
            MedicoId medicoId,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}