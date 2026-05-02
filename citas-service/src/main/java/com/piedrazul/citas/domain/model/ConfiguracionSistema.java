package com.piedrazul.citas.domain.model;

public class ConfiguracionSistema {

    private final Integer semanasDisponibles;

    public ConfiguracionSistema(Integer semanasDisponibles) {
        if (semanasDisponibles == null || semanasDisponibles <= 0) {
            throw new IllegalArgumentException("Semanas inválidas");
        }
        this.semanasDisponibles = semanasDisponibles;
    }

    public Integer getSemanasDisponibles() {
        return semanasDisponibles;
    }
}
