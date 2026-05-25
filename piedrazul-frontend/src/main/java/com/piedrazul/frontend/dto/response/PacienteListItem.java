package com.piedrazul.frontend.dto.response;

public class PacienteListItem {

    private final Long personaId;
    private final String nombreCompleto;

    public PacienteListItem(Long personaId, String nombreCompleto) {
        this.personaId = personaId;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    @Override
    public String toString() {
        return nombreCompleto + " - Paciente #" + personaId;
    }
}
