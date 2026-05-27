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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitaService implements CancelarCitaUseCase,
        ReagendarCitaUseCase, MarcarAsistenciaUseCase, ListarCitasUseCase {

    private final CitaRepositoryPort citaRepository;
    private final PacienteSnapshotRepositoryPort pacienteSnapshotRepository;
    private final MedicoSnapshotRepositoryPort medicoSnapshotRepository;
    private final DisponibilidadSnapshotRepositoryPort disponibilidadSnapshotRepository;
    private final CitaEventPublisherPort eventPublisher;
    private final CitaApplicationMapper mapper;

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

        // Snapshots para enriquecer el evento y la respuesta
        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(cita.getPacienteId()).orElse(null);
        MedicoSnapshot medico = medicoSnapshotRepository.findById(cita.getMedicoId()).orElse(null);

        eventPublisher.publicarCitaCancelada(citaActualizada, paciente, medico);

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

        // Capturamos la fecha original antes de mutar el agregado,
        // para poder publicarla en el evento de reagendamiento.
        LocalDateTime fechaHoraOriginal = cita.getFechaHora();
        LocalDateTime nuevaFechaHora = request.getNuevaFechaHora();

        if (citaRepository.existsCitaActivaByMedicoIdAndFechaHoraExcluding(
                cita.getMedicoId(), nuevaFechaHora, cita.getId())) {
            throw new DisponibilidadNoDisponibleException(
                    "El médico ya tiene una cita agendada en el horario seleccionado");
        }

        if (citaRepository.existsCitaActivaByPacienteIdAndFechaHoraExcluding(
                cita.getPacienteId(), nuevaFechaHora, cita.getId())) {
            throw new PacienteNoDisponibleException(
                    "El paciente ya tiene una cita agendada en el horario seleccionado");
        }

        cita.reagendar(nuevaFechaHora, disponibilidad);

        Cita citaActualizada = citaRepository.save(cita);
        log.info("Cita reagendada exitosamente: {} nueva fecha: {}",
                citaActualizada.getId(), citaActualizada.getFechaHora());

        PacienteSnapshot paciente = pacienteSnapshotRepository.findById(cita.getPacienteId()).orElse(null);
        MedicoSnapshot medico = medicoSnapshotRepository.findById(cita.getMedicoId()).orElse(null);

        eventPublisher.publicarCitaReagendada(citaActualizada, fechaHoraOriginal, paciente, medico);

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
    public List<CitaResponse> listar(Long medicoId, Long pacienteId, LocalDate fecha) {

        List<Cita> citas;

        if (pacienteId != null) {

            if (fecha != null) {
                LocalDateTime inicio = fecha.atStartOfDay();
                LocalDateTime fin = fecha.atTime(23, 59, 59);
                citas = citaRepository.findByPacienteIdAndFecha(
                        PacienteId.of(pacienteId), inicio, fin
                );
            } else {
                citas = citaRepository.findByPacienteId(PacienteId.of(pacienteId));
            }

            if (medicoId != null) {
                MedicoId medicoIdVo = MedicoId.of(medicoId);
                citas = citas.stream()
                        .filter(c -> c.getMedicoId().equals(medicoIdVo))
                        .toList();
            }

        } else if (medicoId != null && fecha != null) {

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