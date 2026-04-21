package com.piedrazul.usuarios.application.service;

import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.domain.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public ConsultarUsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario consultarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id es obligatorio");
        }

        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
