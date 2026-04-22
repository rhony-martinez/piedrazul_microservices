package com.piedrazul.citas.infrastructure.messaging.consumer;

import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.domain.model.*;
import com.piedrazul.citas.domain.valueobjects.*;
import com.piedrazul.citas.infrastructure.messaging.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonaEventConsumer {

    private final PacienteSnapshotRepositoryPort pacienteSnapshotRepository;
    private final MedicoSnapshotRepositoryPort medicoSnapshotRepository;

    @RabbitListener(queues = "${rabbitmq.queue.paciente-creado}")
    public void consumePacienteCreado(PacienteCreadoEvent event) {
        log.info("Recibido evento PacienteCreado: {}", event.getEventId());

        try {
            PacienteCreadoEvent.PacienteData data = event.getData();

            PacienteSnapshot snapshot = new PacienteSnapshot(
                    PacienteId.of(data.getPacienteId()),
                    data.getNombreCompleto(),
                    data.getEmail(),
                    data.getTelefono(),
                    data.isActivo()
            );

            pacienteSnapshotRepository.save(snapshot);
            log.info("Snapshot de paciente actualizado: {}", data.getPacienteId());

        } catch (Exception e) {
            log.error("Error procesando evento PacienteCreado: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.medico-creado}")
    public void consumeMedicoCreado(MedicoCreadoEvent event) {
        log.info("Recibido evento MedicoCreado: {}", event.getEventId());

        try {
            MedicoCreadoEvent.MedicoData data = event.getData();

            MedicoSnapshot snapshot = new MedicoSnapshot(
                    MedicoId.of(data.getMedicoId()),
                    data.getNombreCompleto(),
                    data.getEmail(),
                    data.getEspecialidad(),
                    EstadoMedico.valueOf(data.getEstado())
            );

            medicoSnapshotRepository.save(snapshot);
            log.info("Snapshot de médico actualizado: {}", data.getMedicoId());

        } catch (Exception e) {
            log.error("Error procesando evento MedicoCreado: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.medico-actualizado}")
    public void consumeMedicoActualizado(MedicoActualizadoEvent event) {
        log.info("Recibido evento MedicoActualizado: {}", event.getEventId());

        try {
            MedicoActualizadoEvent.MedicoActualizadoData data = event.getData();

            MedicoSnapshot snapshot = new MedicoSnapshot(
                    MedicoId.of(data.getMedicoId()),
                    data.getNombreCompleto(),
                    data.getEmail(),
                    data.getEspecialidad(),
                    EstadoMedico.valueOf(data.getEstado())
            );

            medicoSnapshotRepository.save(snapshot);
            log.info("Snapshot de médico actualizado: {}", data.getMedicoId());

        } catch (Exception e) {
            log.error("Error procesando evento MedicoActualizado: {}", e.getMessage(), e);
        }
    }
}