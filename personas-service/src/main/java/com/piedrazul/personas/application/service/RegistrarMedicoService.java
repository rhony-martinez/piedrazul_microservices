package com.piedrazul.personas.application.service;

import com.piedrazul.personas.domain.model.Medico;
import com.piedrazul.personas.domain.model.Persona;
import com.piedrazul.personas.domain.repository.IEspecialidadRepository;
import com.piedrazul.personas.domain.repository.IMedicoRepository;
import com.piedrazul.personas.domain.repository.IPersonaRepository;
import com.piedrazul.personas.infrastructure.messaging.publisher.MedicoEventPublisher;
import com.piedrazul.personas.interfaces.rest.dto.request.RegistrarMedicoRequest;
import com.piedrazul.personas.domain.model.TipoProfesional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegistrarMedicoService {

    private final IPersonaRepository personaRepository;
    private final IMedicoRepository medicoRepository;
    private final IEspecialidadRepository especialidadRepository;
    private final MedicoEventPublisher medicoEventPublisher;

    public Medico registrarMedico(RegistrarMedicoRequest request) {
        log.info("Registrando nuevo médico con personaId: {}", request.getPersonaId());

        // Validar que la persona existe
        Persona persona = personaRepository.buscarPorId(request.getPersonaId())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + request.getPersonaId()));

        // Validar que no sea ya un médico
        if (medicoRepository.existePorPersonaId(request.getPersonaId())) {
            throw new RuntimeException("La persona ya está registrada como médico");
        }

        // Crear médico
        Medico medico = Medico.crear(request.getPersonaId(), request.getTipoProfesional());
        Medico medicoGuardado = medicoRepository.guardar(medico);

        log.info("Médico registrado exitosamente con ID: {}", request.getPersonaId());

        // Obtener especialidad
        String especialidadNombre = "Cardiología";
        try {
            var especialidades = especialidadRepository.listarTodos();
            if (!especialidades.isEmpty()) {
                especialidadNombre = especialidades.get(0).getNombre();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener especialidad, usando valor por defecto: {}", e.getMessage());
        }

        // Publicar evento para citas-service (ASÍNCRONO)
        String nombreCompleto = persona.getPrimerNombre() + " " + persona.getPrimerApellido();
        medicoEventPublisher.publicarMedicoCreado(
                request.getPersonaId(),
                nombreCompleto,
                persona.getCorreo(),
                especialidadNombre
        );

        log.info("Evento MedicoCreado publicado para ID: {}", request.getPersonaId());

        return medicoGuardado;  // ← Devuelve el médico creado
    }
}