// personas-service/src/main/java/com/piedrazul/personas/interfaces/rest/controller/DisponibilidadController.java
package com.piedrazul.personas.interfaces.rest.controller;

import com.piedrazul.personas.application.service.ConsultarDisponibilidadesService;
import com.piedrazul.personas.application.service.CrearDisponibilidadService;
import com.piedrazul.personas.interfaces.rest.dto.request.CrearDisponibilidadRequest;
import com.piedrazul.personas.interfaces.rest.dto.response.DisponibilidadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final CrearDisponibilidadService crearDisponibilidadService;
    private final ConsultarDisponibilidadesService consultarDisponibilidadesService;

    @PostMapping
    public ResponseEntity<Void> crearDisponibilidad(@Valid @RequestBody CrearDisponibilidadRequest request) {
        crearDisponibilidadService.crearDisponibilidad(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadResponse>> listar() {

        List<DisponibilidadResponse> response =
                consultarDisponibilidadesService.consultarTodas()
                        .stream()
                        .map(disponibilidad -> new DisponibilidadResponse(
                                disponibilidad.getMedicoId(),
                                disponibilidad.getDiaSemana(),
                                disponibilidad.getHoraInicio().toString(),
                                disponibilidad.getHoraFin().toString(),
                                disponibilidad.getIntervaloMinutos()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }
}