package com.piedrazul.citas.infrastructure.messaging.producer;

import com.piedrazul.citas.application.port.outgoing.CitaEventPublisherPort;
import com.piedrazul.citas.domain.model.Cita;
import com.piedrazul.citas.domain.model.MedicoSnapshot;
import com.piedrazul.citas.domain.model.PacienteSnapshot;
import com.piedrazul.citas.infrastructure.messaging.event.CitaAgendadaEvent;
import com.piedrazul.citas.infrastructure.messaging.event.CitaCanceladaEvent;
import com.piedrazul.citas.infrastructure.messaging.event.CitaReagendadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitaEventPublisherImpl implements CitaEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.citas}")
    private String citasExchange;

    @Value("${rabbitmq.routing.cita-agendada}")
    private String routingKeyAgendada;

    @Value("${rabbitmq.routing.cita-cancelada}")
    private String routingKeyCancelada;

    @Value("${rabbitmq.routing.cita-reagendada}")
    private String routingKeyReagendada;

    @Override
    public void publicarCitaAgendada(Cita cita) {
        log.info("Publicando evento CITA_AGENDADA para cita: {}", cita.getId());

        // Aquí normalmente buscarías los snapshots para obtener emails y nombres
        // Por ahora simulamos con datos básicos
        CitaAgendadaEvent event = CitaAgendadaEvent.create(
                cita.getId().toString(),
                cita.getPacienteId().value(),
                "Paciente Nombre", // Esto vendría del snapshot
                "paciente@email.com", // Esto vendría del snapshot
                cita.getMedicoId().value(),
                "Médico Nombre", // Esto vendría del snapshot
                "medico@email.com", // Esto vendría del snapshot
                cita.getFechaHora(),
                cita.getEstado().getDescripcion()
        );

        rabbitTemplate.convertAndSend(citasExchange, routingKeyAgendada, event);
        log.info("Evento CITA_AGENDADA publicado exitosamente");
    }

    @Override
    public void publicarCitaCancelada(Cita cita) {
        log.info("Publicando evento CITA_CANCELADA para cita: {}", cita.getId());

        CitaCanceladaEvent event = CitaCanceladaEvent.create(
                cita.getId().toString(),
                cita.getPacienteId().value(),
                "paciente@email.com", // Esto vendría del snapshot
                cita.getMedicoId().value(),
                "medico@email.com", // Esto vendría del snapshot
                cita.getFechaHora(),
                cita.getMotivoCancelacion()
        );

        rabbitTemplate.convertAndSend(citasExchange, routingKeyCancelada, event);
        log.info("Evento CITA_CANCELADA publicado exitosamente");
    }

    public void publicarCitaReagendada(Cita cita, LocalDateTime fechaHoraOriginal) {
        log.info("Publicando evento CITA_REAGENDADA para cita: {}", cita.getId());

        CitaReagendadaEvent event = CitaReagendadaEvent.create(
                cita.getId().toString(),
                cita.getPacienteId().value(),
                "paciente@email.com", // Esto vendría del snapshot
                cita.getMedicoId().value(),
                "medico@email.com", // Esto vendría del snapshot
                fechaHoraOriginal,
                cita.getFechaHora()
        );

        rabbitTemplate.convertAndSend(citasExchange, routingKeyReagendada, event);
        log.info("Evento CITA_REAGENDADA publicado exitosamente");
    }
}