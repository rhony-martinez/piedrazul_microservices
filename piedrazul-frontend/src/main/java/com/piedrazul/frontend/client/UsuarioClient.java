package com.piedrazul.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedrazul.frontend.dto.request.CrearUsuarioRequest;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;

public class UsuarioClient {

    private static final String BASE_URL = AuthenticatedHttpClient.baseUrl() + "/api/usuarios";

    private final ObjectMapper mapper = new ObjectMapper();

    public void crearUsuario(CrearUsuarioRequest request) throws Exception {
        String body = mapper.writeValueAsString(request);
        AuthenticatedHttpClient.post(BASE_URL, body);
    }
}
