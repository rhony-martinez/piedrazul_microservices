package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.dto.request.CrearCitaRequest;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.incoming.CrearCitaManualUseCase;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.application.service.agendamiento.AbstractAgendamientoService;
import com.piedrazul.citas.domain.builder.CitaBuilder;
import com.piedrazul.citas.domain.builder.CitaManualBuilder;
import com.piedrazul.citas.domain.model.*;
import org.springframework.stereotype.Service;

@Service
public class CrearCitaManualService
        extends AbstractAgendamientoService
        implements CrearCitaManualUseCase {

    public CrearCitaManualService(
            CitaRepositoryPort citaRepository,
            PacienteSnapshotRepositoryPort pacienteRepository,
            MedicoSnapshotRepositoryPort medicoRepository,
            DisponibilidadSnapshotRepositoryPort disponibilidadRepository,
            CitaEventPublisherPort eventPublisher,
            CitaApplicationMapper mapper
    ) {
        super(
                citaRepository,
                pacienteRepository,
                medicoRepository,
                disponibilidadRepository,
                eventPublisher,
                mapper
        );
    }

    @Override
    protected void validarTipoAgendamiento(
            CrearCitaRequest request,
            PacienteSnapshot paciente,
            MedicoSnapshot medico
    ) {

        // validaciones específicas manuales
        // ejemplo:
        // validar permisos del recepcionista
    }

    @Override
    protected CitaBuilder crearBuilder() {
        return new CitaManualBuilder();
    }

    @Override
    public com.piedrazul.citas.application.dto.response.CitaResponse
    crearCitaManual(CrearCitaRequest request) {

        return super.crearCita(request);
    }
}