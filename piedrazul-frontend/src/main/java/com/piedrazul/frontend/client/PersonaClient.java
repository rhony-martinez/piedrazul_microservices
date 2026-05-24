package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

public class PersonaClient {

    private static final String BASE_URL = AuthenticatedHttpClient.baseUrl() + "/api/personas";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Long crearPersona(CrearPersonaRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.post(BASE_URL, body);

            JsonNode json = objectMapper.readTree(resp.getBody());
            if (!json.has("id")) {
                throw new RuntimeException("Respuesta sin 'id': " + resp.getBody());
            }
            return json.get("id").asLong();

        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error creando persona: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error en PersonaClient: " + e.getMessage(), e);
        }
    }
}
