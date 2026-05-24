package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

import java.util.Map;

public class PacienteClient {

    private static final String URL_PACIENTE = AuthenticatedHttpClient.baseUrl() + "/api/pacientes";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void crearPaciente(Long personaId) {
        try {
            String body = objectMapper.writeValueAsString(Map.of("personaId", personaId));
            AuthenticatedHttpClient.post(URL_PACIENTE, body);
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error creando paciente: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error en PacienteClient: " + e.getMessage(), e);
        }
    }
}
