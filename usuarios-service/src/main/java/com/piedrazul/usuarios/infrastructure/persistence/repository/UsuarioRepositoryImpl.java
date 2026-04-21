package com.piedrazul.usuarios.infrastructure.persistence.repository;

import com.piedrazul.usuarios.domain.model.Rol;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.domain.repository.IUsuarioRepository;
import com.piedrazul.usuarios.infrastructure.persistence.entity.RolEntity;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioEntity;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioRolEntity;
import com.piedrazul.usuarios.infrastructure.persistence.entity.UsuarioRolId;
import com.piedrazul.usuarios.infrastructure.persistence.mapper.UsuarioMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataRolRepository rolRepository;
    private final SpringDataUsuarioRolRepository usuarioRolRepository;

    public UsuarioRepositoryImpl(
            SpringDataUsuarioRepository usuarioRepository,
            SpringDataRolRepository rolRepository,
            SpringDataUsuarioRolRepository usuarioRolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity usuarioEntity;

        if (usuario.getId() == null) {
            usuarioEntity = UsuarioEntity.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPasswordHash())
                    .estado(usuario.getEstado().name())
                    .personaId(usuario.getPersonaId())
                    .intentosFallidos(usuario.getIntentosFallidos())
                    .build();

            usuarioEntity = usuarioRepository.save(usuarioEntity);

            guardarRoles(usuarioEntity, usuario.getRoles());

            UsuarioEntity recargado = usuarioRepository.findById(usuarioEntity.getId())
                    .orElseThrow(() -> new RuntimeException("No se pudo recargar el usuario guardado"));

            return UsuarioMapper.toDomain(recargado);
        }

        usuarioEntity = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualización"));

        usuarioEntity.setUsername(usuario.getUsername());
        usuarioEntity.setPassword(usuario.getPasswordHash());
        usuarioEntity.setEstado(usuario.getEstado().name());
        usuarioEntity.setPersonaId(usuario.getPersonaId());
        usuarioEntity.setIntentosFallidos(usuario.getIntentosFallidos());

        usuarioEntity.getRoles().clear();
        usuarioRepository.save(usuarioEntity);

        guardarRoles(usuarioEntity, usuario.getRoles());

        UsuarioEntity recargado = usuarioRepository.findById(usuarioEntity.getId())
                .orElseThrow(() -> new RuntimeException("No se pudo recargar el usuario actualizado"));

        return UsuarioMapper.toDomain(recargado);
    }

    private void guardarRoles(UsuarioEntity usuarioEntity, Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        for (Rol rol : roles) {
            RolEntity rolEntity = obtenerRolEntity(rol);

            UsuarioRolEntity usuarioRolEntity = UsuarioRolEntity.builder()
                    .id(new UsuarioRolId(usuarioEntity.getId(), rolEntity.getId()))
                    .usuario(usuarioEntity)
                    .rol(rolEntity)
                    .build();

            usuarioRolRepository.save(usuarioRolEntity);
        }
    }

    private RolEntity obtenerRolEntity(Rol rol) {
        if (rol.getId() != null) {
            return rolRepository.findById(rol.getId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + rol.getId()));
        }

        if (rol.getNombre() != null && !rol.getNombre().isBlank()) {
            return rolRepository.findByNombre(rol.getNombre())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con nombre: " + rol.getNombre()));
        }

        throw new IllegalArgumentException("El rol debe tener id o nombre");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(UsuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(UsuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorPersonaId(Integer personaId) {
        return usuarioRepository.findByPersonaId(personaId)
                .map(UsuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorPersonaId(Integer personaId) {
        return usuarioRepository.existsByPersonaId(personaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toDomain)
                .collect(Collectors.toList());
    }
}