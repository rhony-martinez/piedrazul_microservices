package com.piedrazul.citas.application.port.outgoing;

import com.piedrazul.citas.domain.model.Cita;

public interface CitaEventPublisherPort {
    void publicarCitaAgendada(Cita cita);
    void publicarCitaCancelada(Cita cita);
}