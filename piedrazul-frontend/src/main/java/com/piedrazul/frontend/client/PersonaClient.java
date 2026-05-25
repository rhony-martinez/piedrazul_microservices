package com.piedrazul.frontend.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.config.ApiConfig;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;
import com.piedrazul.frontend.dto.response.PersonaResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;
import com.piedrazul.frontend.util.ApiClientException;
import com.piedrazul.frontend.util.ApiErrorParser;

import java.util.List;

public class PersonaClient {

    private static String personasBaseUrl() {
        return ApiConfig.gatewayBaseUrl() + "/api/personas";
    }

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public Long crearPersona(CrearPersonaRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.post(personasBaseUrl(), body);

            JsonNode json = objectMapper.readTree(resp.getBody());
            if (!json.has("id")) {
                throw new RuntimeException("Respuesta sin 'id': " + resp.getBody());
            }
            return json.get("id").asLong();

        } catch (AuthenticatedHttpClient.HttpException e) {
            ApiErrorParser.ParsedApiError parsed = ApiErrorParser.parse(e.getResponseBody());
            throw new ApiClientException(parsed, e);
        } catch (Exception e) {
            throw new RuntimeException("Error en PersonaClient: " + e.getMessage(), e);
        }
    }

    public void compensarRegistroFallido(Long personaId) {
        try {
            AuthenticatedHttpClient.delete(personasBaseUrl() + "/" + personaId + "/registro-fallido");
        } catch (Exception e) {
            System.err.println("No se pudo revertir persona " + personaId + ": " + e.getMessage());
        }
    }

    public List<PersonaResponse> listarPersonas() {
        try {
            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.get(personasBaseUrl());
            return objectMapper.readValue(
                    resp.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PersonaResponse.class)
            );
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error listando personas: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error en PersonaClient: " + e.getMessage(), e);
        }
    }
}
