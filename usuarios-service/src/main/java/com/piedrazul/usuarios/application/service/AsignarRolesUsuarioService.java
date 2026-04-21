package com.piedrazul.usuarios.application.service;

import com.piedrazul.usuarios.domain.model.Rol;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.domain.repository.IRolRepository;
import com.piedrazul.usuarios.domain.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AsignarRolesUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;

    public AsignarRolesUsuarioService(
            IUsuarioRepository usuarioRepository,
            IRolRepository rolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public Usuario asignarRoles(Integer usuarioId, List<String> nombresRoles) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("usuarioId es obligatorio");
        }

        if (nombresRoles == null || nombresRoles.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un rol");
        }

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        for (String nombreRol : nombresRoles) {
            String nombreNormalizado = nombreRol == null ? null : nombreRol.trim();

            if (nombreNormalizado == null || nombreNormalizado.isBlank()) {
                throw new IllegalArgumentException("Nombre de rol inválido");
            }

            Rol rol = rolRepository.buscarPorNombre(nombreNormalizado)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreNormalizado));

            usuario.asignarRol(rol);
        }

        return usuarioRepository.guardar(usuario);
    }
}