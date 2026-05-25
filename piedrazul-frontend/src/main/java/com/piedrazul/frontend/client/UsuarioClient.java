package com.piedrazul.frontend.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearUsuarioRequest;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;
import com.piedrazul.frontend.util.ApiClientException;
import com.piedrazul.frontend.util.ApiErrorParser;

public class UsuarioClient {

    private static final String BASE_URL = AuthenticatedHttpClient.baseUrl() + "/api/usuarios";

    private final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public void crearUsuario(CrearUsuarioRequest request) {
        try {
            String body = mapper.writeValueAsString(request);
            AuthenticatedHttpClient.post(BASE_URL, body);
        } catch (AuthenticatedHttpClient.HttpException e) {
            ApiErrorParser.ParsedApiError parsed = ApiErrorParser.parse(e.getResponseBody());
            throw new ApiClientException(parsed, e);
        } catch (Exception e) {
            throw new RuntimeException("Error creando usuario: " + e.getMessage(), e);
        }
    }
}
