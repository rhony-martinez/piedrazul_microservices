package com.piedrazul.usuarios.infrastructure.config;

import com.piedrazul.usuarios.infrastructure.persistence.entity.RolEntity;
import com.piedrazul.usuarios.infrastructure.persistence.repository.SpringDataRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(SpringDataRolRepository rolRepository) {
        return args -> {
            crearRolSiNoExiste(rolRepository, "ADMINISTRADOR");
            crearRolSiNoExiste(rolRepository, "AGENDADOR");
            crearRolSiNoExiste(rolRepository, "PACIENTE");
            crearRolSiNoExiste(rolRepository, "MEDICO_TERAPISTA");
        };
    }

    private void crearRolSiNoExiste(SpringDataRolRepository rolRepository, String nombreRol) {
        rolRepository.findByNombre(nombreRol)
                .orElseGet(() -> rolRepository.save(
                        RolEntity.builder()
                                .nombre(nombreRol)
                                .build()
                ));
    }
}
