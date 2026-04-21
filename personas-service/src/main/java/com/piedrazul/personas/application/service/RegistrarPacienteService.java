package com.piedrazul.personas.application.service;

import com.piedrazul.personas.application.exception.PacienteYaRegistradoException;
import com.piedrazul.personas.application.exception.PersonaNoEncontradaException;
import com.piedrazul.personas.domain.model.Paciente;
import com.piedrazul.personas.domain.repository.IPacienteRepository;
import com.piedrazul.personas.domain.repository.IPersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarPacienteService {

    private final IPersonaRepository personaRepository;
    private final IPacienteRepository pacienteRepository;

    @Transactional
    public Paciente ejecutar(Long personaId) {
        personaRepository.buscarPorId(personaId)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe una persona con id: " + personaId
                ));

        if (pacienteRepository.existePorPersonaId(personaId)) {
            throw new PacienteYaRegistradoException(
                    "La persona con id " + personaId + " ya está registrada como paciente"
            );
        }

        Paciente paciente = Paciente.crear(personaId);
        return pacienteRepository.guardar(paciente);
    }
}