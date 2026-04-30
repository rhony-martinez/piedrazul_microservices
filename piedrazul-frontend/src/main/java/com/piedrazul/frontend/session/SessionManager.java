package com.piedrazul.frontend.session;

import com.piedrazul.frontend.dto.response.LoginResponse;

public class SessionManager {

    private static LoginResponse currentUser;

    public static void setCurrentUser(LoginResponse user) {
        currentUser = user;
    }

    public static LoginResponse getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}