package com.piedrazul.citas.application.service.agendamiento;

import com.piedrazul.citas.application.dto.request.CrearCitaRequest;
import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.domain.builder.CitaBuilder;
import com.piedrazul.citas.domain.exception.*;
import com.piedrazul.citas.domain.model.*;
import com.piedrazul.citas.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public abstract class AbstractAgendamientoService {

    protected final CitaRepositoryPort citaRepository;
    protected final PacienteSnapshotRepositoryPort pacienteRepository;
    protected final MedicoSnapshotRepositoryPort medicoRepository;
    protected final DisponibilidadSnapshotRepositoryPort disponibilidadRepository;
    protected final CitaEventPublisherPort eventPublisher;
    protected final CitaApplicationMapper mapper;

    // Template Method
    public final CitaResponse crearCita(CrearCitaRequest request) {

        PacienteId pacienteId = PacienteId.of(request.getPacienteId());
        MedicoId medicoId = MedicoId.of(request.getMedicoId());
        UsuarioId creadoPor = UsuarioId.of(request.getUsuarioCreadorId());

        PacienteSnapshot paciente = obtenerPaciente(pacienteId);

        MedicoSnapshot medico = obtenerMedico(medicoId);

        DisponibilidadSnapshot disponibilidad =
                obtenerDisponibilidad(medicoId);

        validarHorarioDisponible(
                medicoId,
                request.getFechaHora(),
                disponibilidad
        );

        validarTipoAgendamiento(
                request,
                paciente,
                medico
        );

        Cita cita = crearBuilder()
                .conPaciente(pacienteId, paciente)
                .conMedico(medicoId, medico)
                .creadaPor(creadoPor)
                .paraFecha(request.getFechaHora())
                .conDisponibilidad(disponibilidad)
                .build();

        try {

            Cita guardada = citaRepository.save(cita);

            eventPublisher.publicarCitaAgendada(guardada);

            return mapper.toResponse(
                    guardada,
                    paciente,
                    medico
            );

        } catch (DataIntegrityViolationException e) {

            throw new MedicoNoDisponibleException(
                    "Horario ocupado"
            );
        }
    }

    // TEMPLATE HOOKS
    protected abstract void validarTipoAgendamiento(
            CrearCitaRequest request,
            PacienteSnapshot paciente,
            MedicoSnapshot medico
    );

    protected abstract CitaBuilder crearBuilder();

    // MÉTODOS COMUNES
    private PacienteSnapshot obtenerPaciente(PacienteId pacienteId) {

        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() ->
                        new PacienteNoExisteException(
                                "Paciente no encontrado"
                        ));
    }

    private MedicoSnapshot obtenerMedico(MedicoId medicoId) {

        return medicoRepository.findById(medicoId)
                .orElseThrow(() ->
                        new MedicoNoDisponibleException(
                                "Médico no encontrado"
                        ));
    }

    private DisponibilidadSnapshot obtenerDisponibilidad(
            MedicoId medicoId
    ) {

        return disponibilidadRepository
                .findByMedicoId(medicoId)
                .orElseThrow(() ->
                        new DisponibilidadNoDisponibleException(
                                "No hay disponibilidad"
                        ));
    }

    private void validarHorarioDisponible(
            MedicoId medicoId,
            java.time.LocalDateTime fechaHora,
            DisponibilidadSnapshot disponibilidad
    ) {

        if (citaRepository.existsByMedicoIdAndFechaHora(
                medicoId,
                fechaHora
        )) {

            throw new DisponibilidadNoDisponibleException(
                    "Horario ocupado"
            );
        }

        if (!disponibilidad.esSlotValido(fechaHora)) {

            throw new DisponibilidadNoDisponibleException(
                    "Slot inválido"
            );
        }
    }
}