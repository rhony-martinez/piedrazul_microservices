package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearUsuarioRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsuarioClient {

    private static final String BASE_URL = "http://localhost:8081/api/usuarios";
    private final ObjectMapper mapper = new ObjectMapper();

    public void crearUsuario(CrearUsuarioRequest request) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(request));
        }

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            throw new RuntimeException("Error creando usuario");
        }
    }
}