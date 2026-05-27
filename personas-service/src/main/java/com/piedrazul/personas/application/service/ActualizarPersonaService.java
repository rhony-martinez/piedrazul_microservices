package com.piedrazul.personas.application.service;

import com.piedrazul.personas.application.exception.PersonaNoEncontradaException;
import com.piedrazul.personas.domain.model.Persona;
import com.piedrazul.personas.domain.repository.IPersonaRepository;
import com.piedrazul.personas.interfaces.rest.dto.request.ActualizarPersonaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActualizarPersonaService {

    private final IPersonaRepository personaRepository;

    public Persona ejecutar(Long personaId, ActualizarPersonaRequest request) {
        Persona persona = personaRepository.buscarPorId(personaId)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe una persona con id: " + personaId
                ));

        persona.actualizarDatosAdministrables(
                request.getPrimerNombre(),
                request.getSegundoNombre(),
                request.getPrimerApellido(),
                request.getSegundoApellido(),
                request.getFechaNacimiento(),
                request.getTelefono()
        );

        return personaRepository.guardar(persona);
    }
}
