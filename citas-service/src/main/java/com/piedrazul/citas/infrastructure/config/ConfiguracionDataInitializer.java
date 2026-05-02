package com.piedrazul.citas.infrastructure.config;

import com.piedrazul.citas.infrastructure.persistence.entity.ConfiguracionSistemaEntity;
import com.piedrazul.citas.infrastructure.persistence.repository.SpringDataConfiguracionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracionDataInitializer {

    private final SpringDataConfiguracionRepository repository;

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            ConfiguracionSistemaEntity config = new ConfiguracionSistemaEntity();
            config.setId(1L);
            config.setSemanasDisponibles(4);
            repository.save(config);
        }
    }
}
