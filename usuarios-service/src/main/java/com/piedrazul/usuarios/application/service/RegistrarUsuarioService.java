package com.piedrazul.usuarios.application.service;

import com.piedrazul.usuarios.application.security.PasswordHasher;
import com.piedrazul.usuarios.application.security.PasswordPolicyValidator;
import com.piedrazul.usuarios.domain.model.EstadoUsuario;
import com.piedrazul.usuarios.domain.model.Rol;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.domain.repository.IRolRepository;
import com.piedrazul.usuarios.domain.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RegistrarUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public RegistrarUsuarioService(
            IUsuarioRepository usuarioRepository,
            IRolRepository rolRepository,
            PasswordHasher passwordHasher,
            PasswordPolicyValidator passwordPolicyValidator
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordHasher = passwordHasher;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    public Usuario registrar(
            Integer personaId,
            String username,
            String password,
            List<String> nombresRoles
    ) {

        String usernameNormalizado = username == null ? null : username.trim();

        if (personaId == null) {
            throw new IllegalArgumentException("personaId es obligatorio");
        }

        if (usernameNormalizado == null || usernameNormalizado.isBlank()) {
            throw new IllegalArgumentException("username es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password es obligatorio");
        }

        passwordPolicyValidator.validar(password);

        if (nombresRoles == null || nombresRoles.isEmpty()) {
            throw new IllegalArgumentException("Debe asignarse al menos un rol");
        }

        if (usuarioRepository.existePorUsername(usernameNormalizado)) {
            throw new IllegalArgumentException("El username ya existe");
        }

        if (usuarioRepository.existePorPersonaId(personaId)) {
            throw new IllegalArgumentException("Ya existe un usuario para ese personaId");
        }

        Set<Rol> roles = new HashSet<>();
        for (String nombreRol : nombresRoles) {
            String nombreRolNormalizado = nombreRol == null ? null : nombreRol.trim();
            if (nombreRolNormalizado == null || nombreRolNormalizado.isBlank()) {
                throw new IllegalArgumentException("Nombre de rol inválido");
            }
            Rol rol = rolRepository.buscarPorNombre(nombreRolNormalizado)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRolNormalizado));
            roles.add(rol);
        }

        Usuario usuario = Usuario.builder()
                .personaId(personaId)
                .username(usernameNormalizado)
                .passwordHash(passwordHasher.hash(password))
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidos(0)
                .roles(roles)
                .build();

        return usuarioRepository.guardar(usuario);
    }
}
