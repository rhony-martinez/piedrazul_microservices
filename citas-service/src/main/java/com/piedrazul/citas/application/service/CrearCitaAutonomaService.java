package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.dto.request.CrearCitaRequest;
import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.incoming.CrearCitaAutonomaUseCase;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.application.service.base.BaseCrearCitaService;
import com.piedrazul.citas.domain.factory.CitaManualFactory;
import com.piedrazul.citas.domain.model.Cita;
import com.piedrazul.citas.domain.model.DatosCreacionCita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CrearCitaAutonomaService
        extends BaseCrearCitaService
        implements CrearCitaAutonomaUseCase {

    private final CitaManualFactory citaManualFactory;

    public CrearCitaAutonomaService(
            CitaRepositoryPort citaRepository,
            PacienteSnapshotRepositoryPort pacienteSnapshotRepository,
            MedicoSnapshotRepositoryPort medicoSnapshotRepository,
            DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository,
            CitaEventPublisherPort eventPublisher,
            CitaApplicationMapper mapper,
            CitaManualFactory citaManualFactory
    ) {

        super(
                citaRepository,
                pacienteSnapshotRepository,
                medicoSnapshotRepository,
                disponibilidadSnapshotRepository,
                eventPublisher,
                mapper
        );

        this.citaManualFactory = citaManualFactory;
    }

    @Override
    public CitaResponse crearCitaAutonoma(
            CrearCitaRequest request
    ) {

        log.info("Creando cita MANUAL");

        DatosCreacionCita datos =
                prepararDatos(request);

        Cita cita = citaManualFactory
                .crearBuilder()
                .conPaciente(
                        datos.pacienteId(),
                        datos.paciente()
                )
                .conMedico(
                        datos.medicoId(),
                        datos.medico()
                )
                .creadaPor(
                        datos.creadoPor()
                )
                .paraFecha(
                        request.getFechaHora()
                )
                .conDisponibilidad(
                        datos.disponibilidad()
                )
                .build();

        return guardar(
                cita,
                datos.paciente(),
                datos.medico()
        );
    }
}