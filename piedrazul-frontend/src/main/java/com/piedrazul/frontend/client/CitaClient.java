package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.CitaResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

import java.time.LocalDateTime;
import java.util.List;

public class CitaClient {

    private static final String BASE_URL = AuthenticatedHttpClient.baseUrl() + "/api/citas";

    private final ObjectMapper objectMapper;

    public CitaClient() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<LocalDateTime> obtenerSlotsDisponibles(Long medicoId) {
        try {
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.get(BASE_URL + "/medicos/" + medicoId + "/slots");
            return objectMapper.readValue(
                    resp.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, LocalDateTime.class)
            );
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error obteniendo slots: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener slots disponibles: " + e.getMessage(), e);
        }
    }

    public CitaResponse crearCitaAutonoma(CrearCitaAutonomaRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.post(BASE_URL + "/autonoma", body);
            return objectMapper.readValue(resp.getBody(), CitaResponse.class);
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error creando cita: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear cita autonoma: " + e.getMessage(), e);
        }
    }

    public List<CitaResponse> listarPorPaciente(Long pacienteId) {
        try {
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.get(BASE_URL + "/historial?pacienteId=" + pacienteId);
            return objectMapper.readValue(
                    resp.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CitaResponse.class)
            );
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error listando citas: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al listar citas del paciente: " + e.getMessage(), e);
        }
    }
}
