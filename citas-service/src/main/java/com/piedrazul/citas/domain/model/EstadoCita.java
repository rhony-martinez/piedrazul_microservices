package com.piedrazul.citas.domain.model;

public enum EstadoCita {
    PROGRAMADA("Programada"),
    CONFIRMADA("Confirmada"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada"),
    NO_ASISTIDA("No Asistida"),
    REAGENDADA("Reagendada");

    private final String descripcion;

    EstadoCita(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean puedeCancelarse() {
        return this == PROGRAMADA || this == CONFIRMADA || this == REAGENDADA;
    }

    public boolean puedeReagendarse() {
        return this == PROGRAMADA || this == CONFIRMADA;
    }
}