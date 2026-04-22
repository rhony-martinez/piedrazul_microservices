package com.piedrazul.personas.infrastructure.messaging.publisher;

import com.piedrazul.personas.infrastructure.messaging.event.MedicoCreadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.personas}")
    private String personasExchange;

    public void publicarMedicoCreado(Long medicoId, String nombreCompleto, String email, String especialidad) {
        log.info("Publicando evento MedicoCreado para médico: {}", medicoId);

        MedicoCreadoEvent event = MedicoCreadoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("MEDICO_CREADO")
                .timestamp(LocalDateTime.now())
                .data(MedicoCreadoEvent.MedicoData.builder()
                        .medicoId(medicoId)
                        .nombreCompleto(nombreCompleto)
                        .email(email)
                        .especialidad(especialidad)
                        .estado("ACTIVO")
                        .build())
                .build();

        rabbitTemplate.convertAndSend(personasExchange, "medico.creado", event);
        log.info("Evento MedicoCreado publicado exitosamente");
    }
}