package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.ConfiguracionRequest;
import com.piedrazul.frontend.dto.response.ConfiguracionResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

public class ConfiguracionClient {

    private static final String URL_CONFIG = AuthenticatedHttpClient.baseUrl() + "/api/configuracion";

    private final ObjectMapper mapper = new ObjectMapper();

    public ConfiguracionResponse obtenerConfiguracion() {
        try {
            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.get(URL_CONFIG);
            return mapper.readValue(resp.getBody(), ConfiguracionResponse.class);
        } catch (AuthenticatedHttpClient.HttpException e) {
            // Mantener comportamiento anterior: si no hay configuracion (404 o similar), devolver null.
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void guardarConfiguracion(Integer semanas) {
        try {
            ConfiguracionRequest request = new ConfiguracionRequest();
            request.setSemanasDisponibles(semanas);

            String body = mapper.writeValueAsString(request);
            AuthenticatedHttpClient.put(URL_CONFIG, body);
        } catch (AuthenticatedHttpClient.HttpException e) {
            throw new RuntimeException("Error guardando configuracion: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
