package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.CitaResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class CitaClient {

    private static final String BASE_URL = "http://localhost:8083/api/citas";
    private final ObjectMapper objectMapper;

    public CitaClient() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // Obtener slots disponibles para un médico
    public List<LocalDateTime> obtenerSlotsDisponibles(Long medicoId) {
        try {
            URL url = new URL(BASE_URL + "/medicos/" + medicoId + "/slots");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error obteniendo slots");
            }

            return objectMapper.readValue(
                    conn.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, LocalDateTime.class)
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener slots disponibles: " + e.getMessage(), e);
        }
    }

    // Crear cita autónoma
    public CitaResponse crearCitaAutonoma(CrearCitaAutonomaRequest request) {
        try {
            URL url = new URL(BASE_URL + "/autonoma");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(request));
            }

            int status = conn.getResponseCode();

            if (status == 201 || status == 200) {
                return objectMapper.readValue(conn.getInputStream(), CitaResponse.class);
            } else {
                String error = new String(conn.getErrorStream().readAllBytes());
                throw new RuntimeException("Error creando cita: " + error);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al crear cita autónoma: " + e.getMessage(), e);
        }
    }
}