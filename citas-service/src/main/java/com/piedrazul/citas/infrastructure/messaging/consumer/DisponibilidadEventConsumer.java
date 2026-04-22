package com.piedrazul.citas.infrastructure.messaging.consumer;

import com.piedrazul.citas.application.port.outgoing.DisponibilidadSnapshotRepositoryPort;
import com.piedrazul.citas.domain.model.DisponibilidadSnapshot;
import com.piedrazul.citas.domain.model.TimeRange;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import com.piedrazul.citas.infrastructure.messaging.event.DisponibilidadActualizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisponibilidadEventConsumer {

    private final DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository;

    // Mapa de conversión de español a inglés
    private static final Map<String, String> DIAS_ESPAÑOL_INGLES = new HashMap<>();

    static {
        DIAS_ESPAÑOL_INGLES.put("LUNES", "MONDAY");
        DIAS_ESPAÑOL_INGLES.put("MARTES", "TUESDAY");
        DIAS_ESPAÑOL_INGLES.put("MIERCOLES", "WEDNESDAY");
        DIAS_ESPAÑOL_INGLES.put("JUEVES", "THURSDAY");
        DIAS_ESPAÑOL_INGLES.put("VIERNES", "FRIDAY");
        DIAS_ESPAÑOL_INGLES.put("SABADO", "SATURDAY");
        DIAS_ESPAÑOL_INGLES.put("DOMINGO", "SUNDAY");
        // Con tilde
        DIAS_ESPAÑOL_INGLES.put("MIÉRCOLES", "WEDNESDAY");
    }

    @RabbitListener(queues = "${rabbitmq.queue.disponibilidad-actualizada}")
    public void consumeDisponibilidadActualizada(DisponibilidadActualizadaEvent event) {
        log.info("Recibido evento DisponibilidadActualizada: {}", event.getEventId());

        try {
            DisponibilidadActualizadaEvent.DisponibilidadData data = event.getData();
            MedicoId medicoId = MedicoId.of(data.getMedicoId());

            DisponibilidadSnapshot snapshot = new DisponibilidadSnapshot(medicoId);

            // Procesar horarios semanales
            if (data.getHorariosSemanales() != null) {
                data.getHorariosSemanales().forEach((diaEspanol, rangos) -> {
                    // Convertir día de español a inglés
                    String diaIngles = DIAS_ESPAÑOL_INGLES.getOrDefault(diaEspanol.toUpperCase(), diaEspanol.toUpperCase());
                    DayOfWeek dia = DayOfWeek.valueOf(diaIngles);

                    rangos.forEach(rango -> {
                        LocalTime start = LocalTime.parse(rango.getStart());
                        LocalTime end = LocalTime.parse(rango.getEnd());
                        snapshot.agregarHorarioSemanal(dia, new TimeRange(start, end));
                    });
                });
            }

            // Procesar bloqueos específicos
            if (data.getBloqueosEspecificos() != null) {
                data.getBloqueosEspecificos().forEach(snapshot::agregarBloqueo);
            }

            disponibilidadSnapshotRepository.save(snapshot);
            log.info("Snapshot de disponibilidad actualizado para médico: {}", data.getMedicoId());

        } catch (Exception e) {
            log.error("Error procesando evento DisponibilidadActualizada: {}", e.getMessage(), e);
        }
    }
}