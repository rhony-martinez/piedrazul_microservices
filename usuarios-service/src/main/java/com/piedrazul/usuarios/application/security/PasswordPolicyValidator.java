package com.piedrazul.usuarios.application.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    public void validar(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        boolean tieneDigito = password.matches(".*\\d.*");
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneEspecial = password.matches(".*[^a-zA-Z0-9].*");

        if (!tieneDigito || !tieneMayuscula || !tieneEspecial) {
            throw new IllegalArgumentException(
                    "La contraseña debe incluir al menos una mayúscula, un número y un carácter especial"
            );
        }
    }
}
