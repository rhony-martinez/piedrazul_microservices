package com.piedrazul.usuarios.interfaces.rest.controller;

import com.piedrazul.usuarios.application.service.AutenticarUsuarioService;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.interfaces.rest.dto.request.LoginRequest;
import com.piedrazul.usuarios.interfaces.rest.dto.response.LoginResponse;
import com.piedrazul.usuarios.interfaces.rest.mapper.UsuarioRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUsuarioService autenticarUsuarioService;

    public AuthController(AutenticarUsuarioService autenticarUsuarioService) {
        this.autenticarUsuarioService = autenticarUsuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = autenticarUsuarioService.autenticar(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(UsuarioRestMapper.toLoginResponse(usuario));
    }
}
