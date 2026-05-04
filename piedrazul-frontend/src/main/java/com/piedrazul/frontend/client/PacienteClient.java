package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PacienteClient {

    private static final String URL_PACIENTE = "http://localhost:8082/api/pacientes";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void crearPaciente(Long personaId) {
        try {
            URL url = new URL(URL_PACIENTE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> body = Map.of("personaId", personaId);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(body));
            }

            int status = conn.getResponseCode();

            if (status != 200 && status != 201) {
                String error = new String(conn.getErrorStream().readAllBytes());
                throw new RuntimeException("Error creando paciente: " + error);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en PacienteClient", e);
        }
    }
}