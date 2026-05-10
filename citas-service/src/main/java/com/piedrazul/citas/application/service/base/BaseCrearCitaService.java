package com.piedrazul.citas.application.service.base;

import com.piedrazul.citas.application.dto.request.CrearCitaRequest;
import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.domain.exception.*;
import com.piedrazul.citas.domain.model.*;
import com.piedrazul.citas.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@RequiredArgsConstructor
public abstract class BaseCrearCitaService {

    protected final CitaRepositoryPort citaRepository;
    protected final PacienteSnapshotRepositoryPort pacienteSnapshotRepository;
    protected final MedicoSnapshotRepositoryPort medicoSnapshotRepository;
    protected final DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository;
    protected final CitaEventPublisherPort eventPublisher;
    protected final CitaApplicationMapper mapper;

    protected DatosCreacionCita prepararDatos(
            CrearCitaRequest request
    ) {

        PacienteId pacienteId =
                PacienteId.of(request.getPacienteId());

        MedicoId medicoId =
                MedicoId.of(request.getMedicoId());

        UsuarioId creadoPor =
                UsuarioId.of(request.getUsuarioCreadorId());

        PacienteSnapshot paciente =
                pacienteSnapshotRepository.findById(pacienteId)
                        .orElseThrow(() ->
                                new PacienteNoExisteException(
                                        "Paciente no encontrado"
                                ));

        MedicoSnapshot medico =
                medicoSnapshotRepository.findById(medicoId)
                        .orElseThrow(() ->
                                new MedicoNoDisponibleException(
                                        "Médico no encontrado"
                                ));

        DisponibilidadSnapshot disponibilidad =
                disponibilidadSnapshotRepository
                        .findByMedicoId(medicoId)
                        .orElseThrow(() ->
                                new DisponibilidadNoDisponibleException(
                                        "No hay disponibilidad"
                                ));

        validarDisponibilidad(
                medicoId,
                request,
                disponibilidad
        );

        return new DatosCreacionCita(
                pacienteId,
                medicoId,
                creadoPor,
                paciente,
                medico,
                disponibilidad
        );
    }

    private void validarDisponibilidad(
            MedicoId medicoId,
            CrearCitaRequest request,
            DisponibilidadSnapshot disponibilidad
    ) {

        if (citaRepository.existsByMedicoIdAndFechaHora(
                medicoId,
                request.getFechaHora()
        )) {

            throw new DisponibilidadNoDisponibleException(
                    "Horario ocupado"
            );
        }

        if (!disponibilidad.esSlotValido(
                request.getFechaHora()
        )) {

            throw new DisponibilidadNoDisponibleException(
                    "Slot inválido"
            );
        }
    }

    protected CitaResponse guardar(
            Cita cita,
            PacienteSnapshot paciente,
            MedicoSnapshot medico
    ) {

        try {

            Cita citaGuardada =
                    citaRepository.save(cita);

            eventPublisher.publicarCitaAgendada(
                    citaGuardada
            );

            return mapper.toResponse(
                    citaGuardada,
                    paciente,
                    medico
            );

        } catch (DataIntegrityViolationException e) {

            throw new MedicoNoDisponibleException(
                    "Horario ya ocupado"
            );
        }
    }
}