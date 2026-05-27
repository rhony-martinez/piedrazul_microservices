package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piedrazul.frontend.config.ApiConfig;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.CitaResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;
import com.piedrazul.frontend.util.ApiErrorParser;

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

    public List<LocalDateTime> obtenerSlotsDisponibles(Long medicoId, Long pacienteId) {
        try {
            StringBuilder url = new StringBuilder(citasBaseUrl() + "/medicos/" + medicoId + "/slots");
            if (pacienteId != null) {
                url.append("?pacienteId=").append(pacienteId);
            }
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.get(url.toString());
            return objectMapper.readValue(
                    resp.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, LocalDateTime.class)
            );
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw apiException("Error obteniendo slots", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener slots disponibles: " + e.getMessage(), e);
        }
    }

    public CitaResponse crearCitaAutonoma(CrearCitaAutonomaRequest request) {
        return crearCita("/autonoma", request);
    }

    public CitaResponse crearCitaManual(CrearCitaAutonomaRequest request) {
        return crearCita("/manual", request);
    }

    private CitaResponse crearCita(String endpoint, CrearCitaAutonomaRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            AuthenticatedHttpClient.Response resp =
                    AuthenticatedHttpClient.post(citasBaseUrl() + endpoint, body);
            return objectMapper.readValue(resp.getBody(), CitaResponse.class);
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw apiException("Error creando cita", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la cita: " + e.getMessage(), e);
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
            throw apiException("Error listando citas", e);
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
            throw apiException("Error listando citas", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al listar citas del médico: " + e.getMessage(), e);
        }
    }

    private static RuntimeException apiException(String context, AuthenticatedHttpClient.HttpException e) {
        ApiErrorParser.ParsedApiError parsed = ApiErrorParser.parse(e.getResponseBody());
        return new RuntimeException(parsed.message(), e);
    }
}
