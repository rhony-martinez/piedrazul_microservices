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
        ConfiguracionSistemaEntity entity = repository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        return new ConfiguracionSistema(entity.getSemanasDisponibles());
    }
}