package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.util.HttpClientUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DisponibilidadClient {

    private static final String URL = "http://localhost:8082/api/disponibilidad";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void crearDisponibilidad(Map<String, Object> body) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(body));
            }

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
                throw new RuntimeException("Error creando disponibilidad");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void crearDisponibilidad(Long medicoId,
                                    String dia,
                                    String horaInicio,
                                    String horaFin,
                                    Integer intervalo) {

        Map<String, Object> body = Map.of(
                "medicoId", medicoId,
                "diaSemana", dia,
                "horaInicio", horaInicio,
                "horaFin", horaFin,
                "intervaloMinutos", intervalo
        );

        crearDisponibilidad(body);
    }
}
