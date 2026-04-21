package com.piedrazul.usuarios.interfaces.rest.controller;

import com.piedrazul.usuarios.application.service.AsignarRolesUsuarioService;
import com.piedrazul.usuarios.application.service.ConsultarUsuarioService;
import com.piedrazul.usuarios.application.service.RegistrarUsuarioService;
import com.piedrazul.usuarios.domain.model.Usuario;
import com.piedrazul.usuarios.interfaces.rest.dto.request.AsignarRolesRequest;
import com.piedrazul.usuarios.interfaces.rest.dto.request.CrearUsuarioRequest;
import com.piedrazul.usuarios.interfaces.rest.dto.response.UsuarioResponse;
import com.piedrazul.usuarios.interfaces.rest.mapper.UsuarioRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final RegistrarUsuarioService registrarUsuarioService;
    private final AsignarRolesUsuarioService asignarRolesUsuarioService;
    private final ConsultarUsuarioService consultarUsuarioService;

    public UsuarioController(
            RegistrarUsuarioService registrarUsuarioService,
            AsignarRolesUsuarioService asignarRolesUsuarioService,
            ConsultarUsuarioService consultarUsuarioService
    ) {
        this.registrarUsuarioService = registrarUsuarioService;
        this.asignarRolesUsuarioService = asignarRolesUsuarioService;
        this.consultarUsuarioService = consultarUsuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = registrarUsuarioService.ejecutar(
                request.getPersonaId(),
                request.getUsername(),
                request.getPassword(),
                request.getRoles()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UsuarioRestMapper.toResponse(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable Integer id) {
        Usuario usuario = consultarUsuarioService.consultarPorId(id);
        return ResponseEntity.ok(UsuarioRestMapper.toResponse(usuario));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UsuarioResponse> asignarRoles(
            @PathVariable Integer id,
            @Valid @RequestBody AsignarRolesRequest request
    ) {
        Usuario usuario = asignarRolesUsuarioService.asignarRoles(id, request.getRoles());
        return ResponseEntity.ok(UsuarioRestMapper.toResponse(usuario));
    }
}
