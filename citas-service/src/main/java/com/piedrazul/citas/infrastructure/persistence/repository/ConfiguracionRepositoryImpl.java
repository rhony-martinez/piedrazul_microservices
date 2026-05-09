package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.application.port.outgoing.ConfiguracionRepositoryPort;
import com.piedrazul.citas.domain.model.ConfiguracionSistema;
import com.piedrazul.citas.infrastructure.persistence.entity.ConfiguracionSistemaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConfiguracionRepositoryImpl implements ConfiguracionRepositoryPort {

    private final SpringDataConfiguracionRepository repository;

    @Override
    public ConfiguracionSistema obtener() {

        return repository.findById(1L)
                .map(entity ->
                        new ConfiguracionSistema(
                                entity.getSemanasDisponibles()
                        )
                )
                .orElse(null);
    }

    @Override
    public ConfiguracionSistema guardar(
            ConfiguracionSistema configuracion
    ) {

        ConfiguracionSistemaEntity entity =
                new ConfiguracionSistemaEntity();

        entity.setId(1L);

        entity.setSemanasDisponibles(
                configuracion.getSemanasDisponibles()
        );

        ConfiguracionSistemaEntity saved =
                repository.save(entity);

        return new ConfiguracionSistema(
                saved.getSemanasDisponibles()
        );
    }
}