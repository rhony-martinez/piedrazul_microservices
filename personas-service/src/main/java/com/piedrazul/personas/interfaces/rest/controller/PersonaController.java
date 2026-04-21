package com.piedrazul.personas.interfaces.rest.controller;

import com.piedrazul.personas.application.service.CrearPersonaService;
import com.piedrazul.personas.application.service.ConsultarPersonaService;
import com.piedrazul.personas.domain.model.Persona;
import com.piedrazul.personas.interfaces.rest.dto.request.CrearPersonaRequest;
import com.piedrazul.personas.interfaces.rest.dto.response.PersonaResponse;
import com.piedrazul.personas.interfaces.rest.mapper.PersonaRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final CrearPersonaService crearPersonaService;
    private final ConsultarPersonaService consultarPersonaService;
    private final PersonaRestMapper mapper;

    @PostMapping
    public PersonaResponse crear(@Valid @RequestBody CrearPersonaRequest request) {
        Persona persona = crearPersonaService.ejecutar(
                request.getPrimerNombre(),
                request.getSegundoNombre(),
                request.getPrimerApellido(),
                request.getSegundoApellido(),
                request.getGenero(),
                request.getFechaNacimiento(),
                request.getTelefono(),
                request.getDni(),
                request.getCorreo()
        );
        return mapper.toResponse(persona);
    }

    @GetMapping("/{id}")
    public PersonaResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(consultarPersonaService.buscarPorId(id));
    }

    @GetMapping
    public List<PersonaResponse> listar() {
        return consultarPersonaService.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}