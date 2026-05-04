package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;

import java.io.InputStream;
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

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(request));
            }

            int status = conn.getResponseCode();

            InputStream responseStream = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody = new String(responseStream.readAllBytes());

            System.out.println("STATUS: " + status);
            System.out.println("RESPONSE: " + responseBody);

            if (status == 200 || status == 201) {

                var json = objectMapper.readTree(responseBody);

                if (json.has("id")) {
                    return json.get("id").asLong();
                } else {
                    throw new RuntimeException("Respuesta sin 'id': " + responseBody);
                }

            } else {
                throw new RuntimeException("Error creando persona: " + responseBody);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en PersonaClient", e);
        }
    }
}