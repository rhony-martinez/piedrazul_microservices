package com.piedrazul.frontend.service;

import com.piedrazul.frontend.client.AuthClient;
import com.piedrazul.frontend.dto.response.LoginResponse;

public class AuthService {

    private final AuthClient authClient = new AuthClient();

    public LoginResponse login(String username, String password) {
        return authClient.login(username, password);
    }
}