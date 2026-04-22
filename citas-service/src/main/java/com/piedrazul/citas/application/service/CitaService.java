package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.dto.request.*;
import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.mapper.CitaApplicationMapper;
import com.piedrazul.citas.application.port.incoming.*;
import com.piedrazul.citas.application.port.outgoing.*;
import com.piedrazul.citas.domain.exception.*;
import com.piedrazul.citas.domain.model.*;
import com.piedrazul.citas.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitaService implements CrearCitaUseCase, CancelarCitaUseCase,
        ReagendarCitaUseCase, MarcarAsistenciaUseCase {

    private final CitaRepositoryPort citaRepository;
    private final PacienteSnapshotRepositoryPort pacienteSnapshotRepository;
    private final MedicoSnapshotRepositoryPort medicoSnapshotRepository;
    private final DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository;
    private final CitaEventPublisherPort eventPublisher;
    private final CitaApplicationMapper mapper;

    @Override
    public CitaResponse crearCita(CrearCitaRequest request) {
        log.info("Creando nueva cita para paciente: {} con medico: {}",
                request.getPacienteId(), request.getMedicoId());

        // 1. Obtener snapshots locales
        PacienteId pacienteId = PacienteId.of(request.getPacienteId());
        MedicoId medicoId = MedicoId.of(request.getMedicoId());
        UsuarioId creadoPor = UsuarioId.of(request.getUsuarioCreadorId());

        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(pacienteId)
                .orElseThrow(() -> new PacienteNoExisteException(
                        "Paciente con ID " + request.getPacienteId() + " no existe"));

        MedicoSnapshot medico = medicoSnapshotRepository.findById(medicoId)
                .orElseThrow(() -> new MedicoNoDisponibleException(
                        "Médico con ID " + request.getMedicoId() + " no existe"));

        DisponibilidadSnapshot disponibilidad = disponibilidadSnapshotRepository.findByMedicoId(medicoId)
                .orElseThrow(() -> new DisponibilidadNoDisponibleException(
                        "No hay disponibilidad configurada para el médico"));

        // 2. Validar no doble agendamiento
        if (citaRepository.existsByMedicoIdAndFechaHora(medicoId, request.getFechaHora())) {
            throw new DisponibilidadNoDisponibleException("El médico ya tiene una cita en ese horario");
        }

        // 3. Crear cita usando factory method del dominio
        Cita cita = Cita.crear(pacienteId, medicoId, creadoPor, request.getFechaHora(),
                paciente, medico, disponibilidad);

        // 4. Persistir cita
        Cita citaGuardada = citaRepository.save(cita);
        log.info("Cita creada exitosamente con ID: {} en estado: {}",
                citaGuardada.getId(), citaGuardada.getEstado().getDescripcion());

        // 5. Publicar evento (comunicación asíncrona)
        eventPublisher.publicarCitaAgendada(citaGuardada);

        // 6. Retornar respuesta
        return mapper.toResponse(citaGuardada, paciente, medico);
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
}