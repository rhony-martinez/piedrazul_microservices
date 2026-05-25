package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piedrazul.frontend.config.ApiConfig;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.CitaResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CitaClient {

    private final ObjectMapper objectMapper;

    /** Base del API Gateway + prefijo de citas-service expuesto por el gateway. */
    private static String citasBaseUrl() {
        return ApiConfig.gatewayBaseUrl() + "/api/citas";
    }

    public CitaClient() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<LocalDateTime> obtenerSlotsDisponibles(Long medicoId) {
        try {
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.get(citasBaseUrl() + "/medicos/" + medicoId + "/slots");
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
                    AuthenticatedHttpClient.post(citasBaseUrl() + "/autonoma", body);
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
                    AuthenticatedHttpClient.get(citasBaseUrl() + "/historial?pacienteId=" + pacienteId);
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

    public List<CitaResponse> listarPorMedico(Long medicoId, Long pacienteId, LocalDate fecha) {
        try {
            StringBuilder url = new StringBuilder(citasBaseUrl() + "/historial?medicoId=" + medicoId);
            if (pacienteId != null) {
                url.append("&pacienteId=").append(pacienteId);
            }
            if (fecha != null) {
                url.append("&fecha=").append(fecha);
            }
            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.get(url.toString());
            return objectMapper.readValue(
                    resp.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CitaResponse.class)
            );
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error listando citas: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al listar citas del médico: " + e.getMessage(), e);
        }
    }
}
