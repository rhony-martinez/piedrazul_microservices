// personas-service/src/main/java/com/piedrazul/personas/infrastructure/messaging/publisher/DisponibilidadEventPublisher.java
package com.piedrazul.personas.infrastructure.messaging.publisher;

import com.piedrazul.personas.infrastructure.messaging.event.DisponibilidadActualizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisponibilidadEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.personas}")
    private String personasExchange;

    public void publicarDisponibilidadActualizada(Long medicoId, String diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        log.info("Publicando evento DisponibilidadActualizada para médico: {}", medicoId);

        // Crear el mapa de horarios semanales
        Map<String, List<Map<String, String>>> horariosSemanales = new HashMap<>();
        List<Map<String, String>> horarios = new ArrayList<>();
        Map<String, String> horario = new HashMap<>();
        horario.put("start", horaInicio.toString());
        horario.put("end", horaFin.toString());
        horarios.add(horario);
        horariosSemanales.put(diaSemana, horarios);

        DisponibilidadActualizadaEvent event = DisponibilidadActualizadaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DISPONIBILIDAD_ACTUALIZADA")
                .timestamp(LocalDateTime.now())
                .data(DisponibilidadActualizadaEvent.DisponibilidadData.builder()
                        .medicoId(medicoId)
                        .horariosSemanales(horariosSemanales)
                        .bloqueosEspecificos(new ArrayList<>())
                        .build())
                .build();

        rabbitTemplate.convertAndSend(personasExchange, "disponibilidad.actualizada", event);
        log.info("Evento DisponibilidadActualizada publicado exitosamente");
    }
}