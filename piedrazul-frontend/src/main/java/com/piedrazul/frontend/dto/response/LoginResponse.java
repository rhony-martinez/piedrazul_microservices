package com.piedrazul.frontend.dto.response;

import java.util.List;

public class LoginResponse {

    private Long id;
    private Long personaId;
    private String username;
    private String estado;
    private List<String> roles;
    private String message;

    public Long getId() { return id; }
    public Long getPersonaId() { return personaId; }
    public String getUsername() { return username; }
    public String getEstado() { return estado; }
    public List<String> getRoles() { return roles; }
    public String getMessage() { return message; }

    public void setId(Long id) { this.id = id; }
    public void setPersonaId(Long personaId) { this.personaId = personaId; }
    public void setUsername(String username) { this.username = username; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void setMessage(String message) { this.message = message; }
}