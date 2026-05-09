package com.piedrazul.citas.interfaces.rest.controller;

import com.piedrazul.citas.application.service.ConfiguracionService;
import com.piedrazul.citas.domain.model.ConfiguracionSistema;
import com.piedrazul.citas.interfaces.rest.dto.request.ActualizarConfiguracionRequest;
import com.piedrazul.citas.interfaces.rest.dto.response.ConfiguracionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService service;

    @GetMapping
    public ConfiguracionResponse obtener() {

        ConfiguracionSistema config = service.obtener();

        return new ConfiguracionResponse(
                config.getSemanasDisponibles()
        );
    }

    @PutMapping
    public ConfiguracionResponse actualizar(
            @RequestBody ActualizarConfiguracionRequest request
    ) {

        ConfiguracionSistema actualizada =
                service.actualizar(
                        request.getSemanasDisponibles()
                );

        return new ConfiguracionResponse(
                actualizada.getSemanasDisponibles()
        );
    }
}