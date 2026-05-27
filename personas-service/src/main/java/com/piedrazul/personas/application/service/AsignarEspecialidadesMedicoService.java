package com.piedrazul.personas.application.service;

import com.piedrazul.personas.application.exception.MedicoNoEncontradoException;
import com.piedrazul.personas.domain.model.EspecialidadMedica;
import com.piedrazul.personas.domain.model.Medico;
import com.piedrazul.personas.domain.repository.IMedicoEspecialidadRepository;
import com.piedrazul.personas.domain.repository.IMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignarEspecialidadesMedicoService {

    private final IMedicoRepository medicoRepository;
    private final IMedicoEspecialidadRepository medicoEspecialidadRepository;

    public Medico ejecutar(Long personaId, Set<EspecialidadMedica> especialidades) {
        Medico medico = medicoRepository.buscarPorPersonaId(personaId)
                .orElseThrow(() -> new MedicoNoEncontradoException(
                        "No existe un médico para la personaId: " + personaId
                ));

        medico.asignarEspecialidades(especialidades);
        medicoEspecialidadRepository.reemplazarEspecialidades(personaId, medico.getEspecialidades());
        return medico;
    }
}
