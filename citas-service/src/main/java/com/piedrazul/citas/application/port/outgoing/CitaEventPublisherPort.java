package com.piedrazul.citas.application.port.outgoing;

import com.piedrazul.citas.domain.model.Cita;

import java.time.LocalDateTime;

public interface CitaEventPublisherPort {
    void publicarCitaAgendada(Cita cita);
    void publicarCitaCancelada(Cita cita);
    void publicarCitaReagendada(Cita cita, LocalDateTime fechaHoraOriginal);
}