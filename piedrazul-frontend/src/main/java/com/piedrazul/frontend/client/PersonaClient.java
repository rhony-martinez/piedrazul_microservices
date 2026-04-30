package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PersonaClient {

    private static final String BASE_URL = "http://localhost:8082/api/personas";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Long crearPersona(CrearPersonaRequest request) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // enviar JSON
            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(request));
            }

            int status = conn.getResponseCode();

            if (status == 200 || status == 201) {

                // ⚠️ Ajusta esto según tu response real
                var response = objectMapper.readTree(conn.getInputStream());

                // suponiendo que devuelves algo como:
                // { "id": 1, ... }
                return response.get("id").asLong();

            } else {
                String error = new String(conn.getErrorStream().readAllBytes());
                throw new RuntimeException("Error creando persona: " + error);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en PersonaClient", e);
        }
    }
}