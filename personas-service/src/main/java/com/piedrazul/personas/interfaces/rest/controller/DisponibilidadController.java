// personas-service/src/main/java/com/piedrazul/personas/interfaces/rest/controller/DisponibilidadController.java
package com.piedrazul.personas.interfaces.rest.controller;

import com.piedrazul.personas.application.service.CrearDisponibilidadService;
import com.piedrazul.personas.interfaces.rest.dto.request.CrearDisponibilidadRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final CrearDisponibilidadService crearDisponibilidadService;

    @PostMapping
    public ResponseEntity<Void> crearDisponibilidad(@Valid @RequestBody CrearDisponibilidadRequest request) {
        crearDisponibilidadService.crearDisponibilidad(request);
        return ResponseEntity.ok().build();
    }
}