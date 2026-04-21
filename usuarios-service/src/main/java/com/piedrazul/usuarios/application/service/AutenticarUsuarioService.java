package com.piedrazul.usuarios.application.service;

import com.piedrazul.usuarios.application.exception.CredencialesInvalidasException;
import com.piedrazul.usuarios.application.security.PasswordHasher;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.domain.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticarUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public AutenticarUsuarioService(
            IUsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(noRollbackFor = CredencialesInvalidasException.class)
    public Usuario autenticar(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password es obligatorio");
        }

        String usernameNormalizado = username.trim();

        Usuario usuario = usuarioRepository.buscarPorUsername(usernameNormalizado)
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!usuario.estaActivo()) {
            throw new IllegalStateException("El usuario está inactivo");
        }

        boolean passwordValido = passwordHasher.matches(password, usuario.getPasswordHash());

        if (!passwordValido) {
            usuario.incrementarIntentosFallidos();
            usuarioRepository.guardar(usuario);
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        usuario.resetearIntentosFallidos();
        return usuarioRepository.guardar(usuario);
    }
}