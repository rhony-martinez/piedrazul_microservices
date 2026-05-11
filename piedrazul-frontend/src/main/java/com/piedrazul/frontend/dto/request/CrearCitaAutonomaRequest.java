package com.piedrazul.frontend.dto.request;

import java.time.LocalDateTime;

public class CrearCitaAutonomaRequest {

    private Long pacienteId;
    private Long medicoId;
    private Long usuarioCreadorId;
    private LocalDateTime fechaHora;

    // Constructor vacío
    public CrearCitaAutonomaRequest() {}

    // Constructor completo
    public CrearCitaAutonomaRequest(Long pacienteId, Long medicoId, Long usuarioCreadorId, LocalDateTime fechaHora) {
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.usuarioCreadorId = usuarioCreadorId;
        this.fechaHora = fechaHora;
    }

    // Getters y Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public Long getUsuarioCreadorId() { return usuarioCreadorId; }
    public void setUsuarioCreadorId(Long usuarioCreadorId) { this.usuarioCreadorId = usuarioCreadorId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}