package com.piedrazul.citas.interfaces.rest.controller;

import com.piedrazul.citas.application.dto.response.CitaResponse;
import com.piedrazul.citas.application.port.incoming.*;
import com.piedrazul.citas.interfaces.rest.dto.request.*;
import com.piedrazul.citas.interfaces.rest.dto.response.CitaRestResponse;
import com.piedrazul.citas.interfaces.rest.mapper.CitaRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CrearCitaUseCase crearCitaUseCase;
    private final CancelarCitaUseCase cancelarCitaUseCase;
    private final ReagendarCitaUseCase reagendarCitaUseCase;
    private final MarcarAsistenciaUseCase marcarAsistenciaUseCase;
    private final CitaRestMapper mapper;

    @PostMapping
    public ResponseEntity<CitaRestResponse> crearCita(@Valid @RequestBody CrearCitaRestRequest request) {
        log.info("REST - Solicitud de creación de cita para paciente: {} con médico: {}",
                request.getPacienteId(), request.getMedicoId());

        CitaResponse response = crearCitaUseCase.crearCita(mapper.toApplicationRequest(request));
        CitaRestResponse restResponse = mapper.toRestResponse(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
    }

    @PutMapping("/{citaId}/cancelar")
    public ResponseEntity<CitaRestResponse> cancelarCita(
            @PathVariable String citaId,
            @RequestParam String motivo) {
        log.info("REST - Solicitud de cancelación de cita: {}", citaId);

        CancelarCitaRestRequest request = CancelarCitaRestRequest.builder()
                .citaId(citaId)
                .motivo(motivo)
                .build();

        CitaResponse response = cancelarCitaUseCase.cancelarCita(mapper.toApplicationRequest(request));
        CitaRestResponse restResponse = mapper.toRestResponse(response);

        return ResponseEntity.ok(restResponse);
    }

    @PutMapping("/reagendar")
    public ResponseEntity<CitaRestResponse> reagendarCita(@Valid @RequestBody ReagendarCitaRestRequest request) {
        log.info("REST - Solicitud de reagendamiento de cita: {} para nueva fecha: {}",
                request.getCitaId(), request.getNuevaFechaHora());

        CitaResponse response = reagendarCitaUseCase.reagendarCita(mapper.toApplicationRequest(request));
        CitaRestResponse restResponse = mapper.toRestResponse(response);

        return ResponseEntity.ok(restResponse);
    }

    @PutMapping("/asistencia")
    public ResponseEntity<CitaRestResponse> marcarAsistencia(@Valid @RequestBody MarcarAsistenciaRestRequest request) {
        log.info("REST - Solicitud de marcación de asistencia para cita: {}, asistió: {}",
                request.getCitaId(), request.getAsistio());

        CitaResponse response = marcarAsistenciaUseCase.marcarAsistencia(mapper.toApplicationRequest(request));
        CitaRestResponse restResponse = mapper.toRestResponse(response);

        return ResponseEntity.ok(restResponse);
    }
}