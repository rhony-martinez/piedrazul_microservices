package com.piedrazul.personas.infrastructure.config;

import com.piedrazul.personas.application.service.CrearPersonaService;
import com.piedrazul.personas.application.service.RegistrarMedicoService;
import com.piedrazul.personas.application.service.RegistrarPacienteService;
import com.piedrazul.personas.domain.model.Genero;
import com.piedrazul.personas.domain.model.Persona;
import com.piedrazul.personas.domain.model.TipoProfesional;
import com.piedrazul.personas.domain.repository.IPersonaRepository;


import com.piedrazul.personas.interfaces.rest.dto.request.RegistrarMedicoRequest;
import com.piedrazul.personas.interfaces.rest.dto.request.RegistrarPacienteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CrearPersonaService crearPersonaService;
    private final RegistrarPacienteService registrarPacienteService;
    private final RegistrarMedicoService registrarMedicoService;
    private final IPersonaRepository personaRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            log.info("🚀 Iniciando carga de datos iniciales...");

            // ======================
            // 1. CREAR PERSONAS
            // ======================

            Long persona1 = crearPersonaSiNoExiste(
                    "Juan", "Carlos", "Perez", "Lopez",
                    Genero.HOMBRE, "12345678", "juan@mail.com"
            );

            Long persona2 = crearPersonaSiNoExiste(
                    "Maria", "Elena", "Gomez", "Ruiz",
                    Genero.MUJER, "87654321", "maria@mail.com"
            );

            Long persona3 = crearPersonaSiNoExiste(
                    "Pedro", null, "Ramirez", null,
                    Genero.HOMBRE, "11111111", "pedro@mail.com"
            );

            Long persona4 = crearPersonaSiNoExiste(
                    "Laura", null, "Torres", null,
                    Genero.MUJER, "22222222", "laura@mail.com"
            );

            // ======================
            // 2. REGISTRAR PACIENTE
            // ======================
            registrarPacienteSiNoExiste(persona1);

            // ======================
            // 3. REGISTRAR MÉDICO
            // ======================
            registrarMedicoSiNoExiste(persona2);

            log.info("✅ Datos iniciales cargados correctamente");
        };
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private Long crearPersonaSiNoExiste(
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            Genero genero,
            String dni,
            String correo
    ) {

        return personaRepository.buscarPorDni(dni)
                .map(Persona::getId)
                .orElseGet(() -> {
                    log.info("Creando persona con DNI: {}", dni);

                    return crearPersonaService.ejecutar(
                            primerNombre,
                            segundoNombre,
                            primerApellido,
                            segundoApellido,
                            genero,
                            LocalDate.of(1990, 1, 1),
                            "3000000000",
                            dni,
                            correo
                    ).getId();
                });
    }

    private void registrarPacienteSiNoExiste(Long personaId) {
        try {
            RegistrarPacienteRequest request = new RegistrarPacienteRequest();
            request.setPersonaId(personaId);
            registrarPacienteService.registrarPaciente(request);
        } catch (Exception e) {
            log.warn("Paciente ya existe o no se pudo crear (personaId={}): {}", personaId, e.getMessage());
        }
    }

    private void registrarMedicoSiNoExiste(Long personaId) {
        try {
            RegistrarMedicoRequest request = new RegistrarMedicoRequest();

            request.setPersonaId(personaId);
            request.setTipoProfesional(TipoProfesional.MEDICO);
            registrarMedicoService.registrarMedico(request);

        } catch (Exception e) {
            log.warn("Médico ya existe o no se pudo crear (personaId={}): {}", personaId, e.getMessage());
        }
    }
}