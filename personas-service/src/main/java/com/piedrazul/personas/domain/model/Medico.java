package com.piedrazul.personas.domain.model;

import java.util.Objects;

public class Medico {

    private Long personaId;
    private TipoProfesional tipoProfesional;
    private EstadoMedico estado;

    public Medico() {
    }

    public Medico(Long personaId, TipoProfesional tipoProfesional, EstadoMedico estado) {
        validarPersonaId(personaId);
        validarTipoProfesional(tipoProfesional);
        validarEstado(estado);

        this.personaId = personaId;
        this.tipoProfesional = tipoProfesional;
        this.estado = estado;
    }

    public static Medico crear(Long personaId, TipoProfesional tipoProfesional) {
        return new Medico(personaId, tipoProfesional, EstadoMedico.ACTIVO);
    }

    public void cambiarEstado(EstadoMedico nuevoEstado) {
        validarEstado(nuevoEstado);
        this.estado = nuevoEstado;
    }

    private void validarPersonaId(Long personaId) {
        if (personaId == null || personaId <= 0) {
            throw new IllegalArgumentException("El personaId del médico es obligatorio");
        }
    }

    private void validarTipoProfesional(TipoProfesional tipoProfesional) {
        if (tipoProfesional == null) {
            throw new IllegalArgumentException("El tipo profesional es obligatorio");
        }
    }

    private void validarEstado(EstadoMedico estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del médico es obligatorio");
        }
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        validarPersonaId(personaId);
        this.personaId = personaId;
    }

    public TipoProfesional getTipoProfesional() {
        return tipoProfesional;
    }

    public void setTipoProfesional(TipoProfesional tipoProfesional) {
        validarTipoProfesional(tipoProfesional);
        this.tipoProfesional = tipoProfesional;
    }

    public EstadoMedico getEstado() {
        return estado;
    }

    public void setEstado(EstadoMedico estado) {
        validarEstado(estado);
        this.estado = estado;
    }

    public boolean estaActivo() {
        return EstadoMedico.ACTIVO.equals(this.estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medico medico)) return false;
        return Objects.equals(personaId, medico.personaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaId);
    }
}