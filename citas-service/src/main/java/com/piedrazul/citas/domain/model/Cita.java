package com.piedrazul.citas.domain.model;

import com.piedrazul.citas.domain.exception.*;
import com.piedrazul.citas.domain.valueobjects.*;
import java.time.LocalDateTime;

public class Cita {
    private final CitaId id;
    private final PacienteId pacienteId;
    private final MedicoId medicoId;
    private final UsuarioId creadoPor;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private String motivoCancelacion;
    private LocalDateTime fechaAsistencia;
    private AuditMetadata audit;

    // Constructor privado para nueva cita
    private Cita(CitaId id, PacienteId pacienteId, MedicoId medicoId,
                 UsuarioId creadoPor, LocalDateTime fechaHora) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.creadoPor = creadoPor;
        this.fechaHora = fechaHora;
        this.estado = EstadoCita.PROGRAMADA;
        this.audit = AuditMetadata.crear();
    }

    // Constructor privado para reconstruir desde BD
    private Cita(CitaId id, PacienteId pacienteId, MedicoId medicoId,
                 UsuarioId creadoPor, LocalDateTime fechaHora,
                 EstadoCita estado, String motivoCancelacion,
                 LocalDateTime fechaAsistencia, AuditMetadata audit) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.creadoPor = creadoPor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.motivoCancelacion = motivoCancelacion;
        this.fechaAsistencia = fechaAsistencia;
        this.audit = audit;
    }

    // Factory method para crear nueva cita con validaciones
    public static Cita crear(PacienteId pacienteId, MedicoId medicoId,
                             UsuarioId creadoPor, LocalDateTime fechaHora,
                             PacienteSnapshot paciente,
                             MedicoSnapshot medico,
                             DisponibilidadSnapshot disponibilidad) {

        // Validaciones de dominio
        if (!paciente.existe()) {
            throw new PacienteNoExisteException("Paciente no encontrado o inactivo");
        }

        if (!medico.estaActivo()) {
            throw new MedicoNoDisponibleException("Médico no está activo para atender");
        }

        if (!disponibilidad.estaDisponible(medicoId, fechaHora)) {
            throw new DisponibilidadNoDisponibleException("El médico no está disponible en ese horario");
        }

        CitaId nuevoId = CitaId.generate();

        return new Cita(nuevoId, pacienteId, medicoId, creadoPor, fechaHora);
    }

    // Factory method para reconstruir desde base de datos
    public static Cita reconstruir(CitaId id, PacienteId pacienteId, MedicoId medicoId,
                                   UsuarioId creadoPor, LocalDateTime fechaHora,
                                   EstadoCita estado, String motivoCancelacion,
                                   LocalDateTime fechaAsistencia, LocalDateTime createdAt,
                                   LocalDateTime updatedAt, String createdBy) {

        AuditMetadata audit = AuditMetadata.reconstruir(createdAt, updatedAt, createdBy);

        return new Cita(id, pacienteId, medicoId, creadoPor, fechaHora,
                estado, motivoCancelacion, fechaAsistencia, audit);
    }

    // Cancelar cita
    public void cancelar(String motivo) {
        if (!this.estado.puedeCancelarse()) {
            throw new CitaNoCancelableException(
                    String.format("No se puede cancelar una cita en estado: %s", this.estado.getDescripcion())
            );
        }
        this.estado = EstadoCita.CANCELADA;
        this.motivoCancelacion = motivo;
        this.audit.actualizar();
    }

    // Reagendar cita
    public void reagendar(LocalDateTime nuevaFechaHora, DisponibilidadSnapshot disponibilidad) {
        if (!this.estado.puedeReagendarse()) {
            throw new IllegalStateException(
                    String.format("Solo se pueden reagendar citas confirmadas, estado actual: %s",
                            this.estado.getDescripcion())
            );
        }

        if (!disponibilidad.estaDisponible(this.medicoId, nuevaFechaHora)) {
            throw new DisponibilidadNoDisponibleException("Nuevo horario no disponible");
        }

        this.fechaHora = nuevaFechaHora;
        this.estado = EstadoCita.REAGENDADA;
        this.audit.actualizar();
    }

    // Marcar como atendida
    public void marcarComoAtendida() {
        if (this.estado != EstadoCita.CONFIRMADA && this.estado != EstadoCita.REAGENDADA) {
            throw new IllegalStateException(
                    String.format("Solo se pueden marcar como atendidas citas confirmadas o reagendadas, estado actual: %s",
                            this.estado.getDescripcion())
            );
        }
        this.estado = EstadoCita.ATENDIDA;
        this.fechaAsistencia = LocalDateTime.now();
        this.audit.actualizar();
    }

    // Marcar como no asistida
    public void marcarComoNoAsistida() {
        if (this.estado != EstadoCita.CONFIRMADA && this.estado != EstadoCita.REAGENDADA) {
            throw new IllegalStateException(
                    String.format("Solo se pueden marcar como no asistidas citas confirmadas o reagendadas, estado actual: %s",
                            this.estado.getDescripcion())
            );
        }

        // Verificar que la fecha de la cita ya pasó
        if (this.fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede marcar como no asistida una cita futura");
        }

        this.estado = EstadoCita.NO_ASISTIDA;
        this.fechaAsistencia = LocalDateTime.now();
        this.audit.actualizar();
    }

    // Getters
    public CitaId getId() { return id; }
    public PacienteId getPacienteId() { return pacienteId; }
    public MedicoId getMedicoId() { return medicoId; }
    public UsuarioId getCreadoPor() { return creadoPor; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoCita getEstado() { return estado; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public LocalDateTime getFechaAsistencia() { return fechaAsistencia; }
    public AuditMetadata getAudit() { return audit; }
}