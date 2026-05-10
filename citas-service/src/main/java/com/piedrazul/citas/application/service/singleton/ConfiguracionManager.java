package com.piedrazul.citas.application.service.singleton;

import com.piedrazul.citas.application.port.outgoing.ConfiguracionRepositoryPort;
import com.piedrazul.citas.domain.model.ConfiguracionSistema;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracionManager {

    private final ConfiguracionRepositoryPort configuracionRepository;

    private ConfiguracionSistema configuracionCache;

    @PostConstruct
    public void inicializar() {

        configuracionCache = configuracionRepository.obtener();

        if (configuracionCache == null) {

            configuracionCache =
                    configuracionRepository.guardar(
                            new ConfiguracionSistema(4)
                    );
        }
    }

    public ConfiguracionSistema obtenerConfiguracion() {
        return configuracionCache;
    }

    public ConfiguracionSistema actualizarConfiguracion(
            Integer semanasDisponibles
    ) {

        ConfiguracionSistema nuevaConfiguracion =
                new ConfiguracionSistema(semanasDisponibles);

        configuracionCache =
                configuracionRepository.guardar(nuevaConfiguracion);

        return configuracionCache;
    }
}