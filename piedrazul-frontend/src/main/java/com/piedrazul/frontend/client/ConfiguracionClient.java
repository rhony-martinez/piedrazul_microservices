package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.ConfiguracionRequest;
import com.piedrazul.frontend.dto.response.ConfiguracionResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfiguracionClient {

    private static final String URL_CONFIG =
            "http://localhost:8083/api/configuracion";

    private final ObjectMapper mapper = new ObjectMapper();

    public ConfiguracionResponse obtenerConfiguracion() {

        try {

            URL url = new URL(URL_CONFIG);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            return mapper.readValue(
                    conn.getInputStream(),
                    ConfiguracionResponse.class
            );

        } catch (Exception e) {
            return null;
        }
    }

    public void guardarConfiguracion(Integer semanas) {

        try {

            URL url = new URL(URL_CONFIG);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ConfiguracionRequest request =
                    new ConfiguracionRequest();

            request.setSemanasDisponibles(semanas);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(mapper.writeValueAsBytes(request));
            }

            int status = conn.getResponseCode();

            if (status != 200) {
                throw new RuntimeException(
                        "Error guardando configuración"
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}