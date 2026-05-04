package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.LoginRequest;
import com.piedrazul.frontend.dto.response.LoginResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8081/api";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginResponse login(String username, String password) {
        try {
            URL url = URI.create(BASE_URL + "/auth/login").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            LoginRequest request = new LoginRequest(username, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(request));
            }

            int status = conn.getResponseCode();

            if (status == 200) {
                return objectMapper.readValue(conn.getInputStream(), LoginResponse.class);
            } else {
                // leer error del backend
                String error = new String(conn.getErrorStream().readAllBytes());
                throw new RuntimeException("Error auth_register: " + error);
            }

        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el servicio", e);
        }
    }
}