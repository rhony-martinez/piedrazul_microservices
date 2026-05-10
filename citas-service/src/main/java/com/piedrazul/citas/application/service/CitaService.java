package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.dto.request.*;
import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.incoming.*;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.domain.builder.CitaBuilderFactory;
import com.piedrazul.citas.domain.exception.*;
import com.piedrazul.citas.domain.model.*;
import com.piedrazul.citas.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitaService implements CancelarCitaUseCase,
        ReagendarCitaUseCase, MarcarAsistenciaUseCase, ListarCitasUseCase, CrearCitaAutonomaUseCase,
        CrearCitaManualUseCase {

    private final CitaRepositoryPort citaRepository;
    private final PacienteSnapshotRepositoryPort pacienteSnapshotRepository;
    private final MedicoSnapshotRepositoryPort medicoSnapshotRepository;
    private final DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository;
    private final CitaEventPublisherPort eventPublisher;
    private final CitaApplicationMapper mapper;

    private CitaResponse crearCita(
            CrearCitaRequest request,
            TipoAgendamiento tipo
    ) {

        log.info("Creando nueva cita tipo: {}", tipo);

        PacienteId pacienteId = PacienteId.of(request.getPacienteId());
        MedicoId medicoId = MedicoId.of(request.getMedicoId());
        UsuarioId creadoPor = UsuarioId.of(request.getUsuarioCreadorId());

        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(pacienteId)
                .orElseThrow(() -> new PacienteNoExisteException(
                        "Paciente no encontrado"
                ));

        MedicoSnapshot medico = medicoSnapshotRepository.findById(medicoId)
                .orElseThrow(() -> new MedicoNoDisponibleException(
                        "Médico no encontrado"
                ));

        DisponibilidadSnapshot disponibilidad =
                disponibilidadSnapshotRepository.findByMedicoId(medicoId)
                        .orElseThrow(() ->
                                new DisponibilidadNoDisponibleException(
                                        "No hay disponibilidad"
                                ));

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

        Cita cita = CitaBuilderFactory
                .crear(tipo)
                .conPaciente(pacienteId, paciente)
                .conMedico(medicoId, medico)
                .creadaPor(creadoPor)
                .paraFecha(request.getFechaHora())
                .conDisponibilidad(disponibilidad)
                .build();

        try {

            Cita citaGuardada = citaRepository.save(cita);

            eventPublisher.publicarCitaAgendada(citaGuardada);

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

    @Override
    public CitaResponse crearCitaAutonoma(CrearCitaRequest request) {

        return crearCita(
                request,
                TipoAgendamiento.AUTONOMO
        );
    }

    @Override
    public CitaResponse crearCitaManual(CrearCitaRequest request) {

        return crearCita(
                request,
                TipoAgendamiento.MANUAL
        );
    }

    @Override
    public CitaResponse cancelarCita(CancelarCitaRequest request) {
        log.info("Cancelando cita: {}", request.getCitaId());

        CitaId citaId = CitaId.fromString(request.getCitaId());

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNoEncontradaException(
                        "Cita no encontrada con ID: " + request.getCitaId()));

        cita.cancelar(request.getMotivo());

        Cita citaActualizada = citaRepository.save(cita);
        log.info("Cita cancelada exitosamente: {}", citaActualizada.getId());

        // Publicar evento de cancelación
        eventPublisher.publicarCitaCancelada(citaActualizada);

        // Obtener snapshots para respuesta
        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(cita.getPacienteId()).orElse(null);
        MedicoSnapshot medico = medicoSnapshotRepository.findById(cita.getMedicoId()).orElse(null);

        return mapper.toResponse(citaActualizada, paciente, medico);
    }

    @Override
    public CitaResponse reagendarCita(ReagendarCitaRequest request) {
        log.info("Reagendando cita: {} para nueva fecha: {}",
                request.getCitaId(), request.getNuevaFechaHora());

        CitaId citaId = CitaId.fromString(request.getCitaId());

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNoEncontradaException(
                        "Cita no encontrada con ID: " + request.getCitaId()));

        DisponibilidadSnapshot disponibilidad = disponibilidadSnapshotRepository
                .findByMedicoId(cita.getMedicoId())
                .orElseThrow(() -> new DisponibilidadNoDisponibleException(
                        "No hay disponibilidad configurada para el médico"));

        cita.reagendar(request.getNuevaFechaHora(), disponibilidad);

        Cita citaActualizada = citaRepository.save(cita);
        log.info("Cita reagendada exitosamente: {} nueva fecha: {}",
                citaActualizada.getId(), citaActualizada.getFechaHora());

        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(cita.getPacienteId()).orElse(null);
        MedicoSnapshot medico = medicoSnapshotRepository.findById(cita.getMedicoId()).orElse(null);

        return mapper.toResponse(citaActualizada, paciente, medico);
    }

    @Override
    public CitaResponse marcarAsistencia(MarcarAsistenciaRequest request) {
        log.info("Marcando asistencia para cita: {}, asistió: {}",
                request.getCitaId(), request.isAsistio());

        CitaId citaId = CitaId.fromString(request.getCitaId());

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNoEncontradaException(
                        "Cita no encontrada con ID: " + request.getCitaId()));

        if (request.isAsistio()) {
            cita.marcarComoAtendida();
            log.info("Cita marcada como ATENDIDA: {}", cita.getId());
        } else {
            cita.marcarComoNoAsistida();
            log.info("Cita marcada como NO_ASISTIDA: {}", cita.getId());
        }

        Cita citaActualizada = citaRepository.save(cita);

        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(cita.getPacienteId()).orElse(null);
        MedicoSnapshot medico = medicoSnapshotRepository.findById(cita.getMedicoId()).orElse(null);

        return mapper.toResponse(citaActualizada, paciente, medico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar(Long medicoId, LocalDate fecha) {

        List<Cita> citas;

        if (medicoId != null && fecha != null) {

            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.atTime(23, 59, 59);

            citas = citaRepository.findByMedicoIdAndFecha(
                    MedicoId.of(medicoId), inicio, fin
            );

        } else if (medicoId != null) {

            citas = citaRepository.findByMedicoId(MedicoId.of(medicoId));

        } else if (fecha != null) {

            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.atTime(23, 59, 59);

            citas = citaRepository.findByFecha(inicio, fin);

        } else {

            citas = citaRepository.findAll();
        }

        return citas.stream()
                .map(cita -> {
                    PacienteSnapshot paciente = pacienteSnapshotRepository
                            .findById(cita.getPacienteId()).orElse(null);

                    MedicoSnapshot medico = medicoSnapshotRepository
                            .findById(cita.getMedicoId()).orElse(null);

                    return mapper.toResponse(cita, paciente, medico);
                })
                .toList();
    }
}