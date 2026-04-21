package com.piedrazul.personas.application.service;

import com.piedrazul.personas.application.exception.MedicoYaRegistradoException;
import com.piedrazul.personas.application.exception.PersonaNoEncontradaException;
import com.piedrazul.personas.domain.model.Medico;
import com.piedrazul.personas.domain.model.TipoProfesional;
import com.piedrazul.personas.domain.repository.IMedicoRepository;
import com.piedrazul.personas.domain.repository.IPersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarMedicoService {

    private final IPersonaRepository personaRepository;
    private final IMedicoRepository medicoRepository;

    @Transactional
    public Medico ejecutar(Long personaId, TipoProfesional tipoProfesional) {
        personaRepository.buscarPorId(personaId)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe una persona con id: " + personaId
                ));

        if (medicoRepository.existePorPersonaId(personaId)) {
            throw new MedicoYaRegistradoException(
                    "La persona con id " + personaId + " ya está registrada como médico"
            );
        }

        Medico medico = Medico.crear(personaId, tipoProfesional);
        return medicoRepository.guardar(medico);
    }
}