package com.piedrazul.citas.application.service;

import com.piedrazul.citas.application.port.incoming.ActualizarConfiguracionUseCase;
import com.piedrazul.citas.application.port.incoming.ConsultarConfiguracionUseCase;
import com.piedrazul.citas.application.port.outgoing.ConfiguracionRepositoryPort;
import com.piedrazul.citas.domain.model.ConfiguracionSistema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracionService
        implements ConsultarConfiguracionUseCase,
        ActualizarConfiguracionUseCase {

    private final ConfiguracionRepositoryPort configuracionRepository;

    @Override
    public ConfiguracionSistema obtener() {
        return configuracionRepository.obtener();
    }

    @Override
    public ConfiguracionSistema actualizar(Integer semanasDisponibles) {

        validarSemanas(semanasDisponibles);

        ConfiguracionSistema configuracion =
                new ConfiguracionSistema(semanasDisponibles);

        return configuracionRepository.guardar(configuracion);
    }

    private void validarSemanas(Integer semanasDisponibles) {

        if (semanasDisponibles == null || semanasDisponibles <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de semanas debe ser mayor a cero"
            );
        }

        if (semanasDisponibles > 52) {
            throw new IllegalArgumentException(
                    "La cantidad máxima permitida es 52 semanas"
            );
        }
    }
}