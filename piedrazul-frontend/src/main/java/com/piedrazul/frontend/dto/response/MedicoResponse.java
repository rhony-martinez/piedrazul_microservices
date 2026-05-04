package com.piedrazul.frontend.dto.response;

public class MedicoResponse {

    private Long personaId;
    private String tipoProfesional;
    private String estado;

    // GETTERS Y SETTERS
    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getTipoProfesional() {
        return tipoProfesional;
    }

    public void setTipoProfesional(String tipoProfesional) {
        this.tipoProfesional = tipoProfesional;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // CLAVE: lo que se muestra en el ComboBox
    @Override
    public String toString() {
        return "Médico #" + personaId + " (" + tipoProfesional + ")";
    }
}