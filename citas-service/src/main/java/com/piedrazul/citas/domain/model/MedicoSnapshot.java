package com.piedrazul.citas.domain.model;

import com.piedrazul.citas.domain.valueobjects.MedicoId;
import java.time.LocalDateTime;

public class MedicoSnapshot {
    private final MedicoId id;
    private final String nombreCompleto;
    private final String email;
    private final String especialidad;
    private final EstadoMedico estado;
    private final LocalDateTime actualizadoEn;

    public MedicoSnapshot(MedicoId id, String nombreCompleto,
                          String email, String especialidad, EstadoMedico estado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.especialidad = especialidad;
        this.estado = estado;
        this.actualizadoEn = LocalDateTime.now();
    }

    public boolean estaActivo() {
        return estado == EstadoMedico.ACTIVO;
    }

    // Getters
    public MedicoId getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getEspecialidad() { return especialidad; }
    public EstadoMedico getEstado() { return estado; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}