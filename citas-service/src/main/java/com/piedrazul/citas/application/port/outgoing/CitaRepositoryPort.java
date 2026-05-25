package com.piedrazul.citas.application.port.outgoing;

import com.piedrazul.citas.domain.model.Cita;
import com.piedrazul.citas.domain.valueobjects.CitaId;
import com.piedrazul.citas.domain.valueobjects.MedicoId;
import com.piedrazul.citas.domain.valueobjects.PacienteId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepositoryPort {
        Cita save(Cita cita);

        Optional<Cita> findById(CitaId id);

        boolean existsByMedicoIdAndFechaHora(com.piedrazul.citas.domain.valueobjects.MedicoId medicoId,
                        java.time.LocalDateTime fechaHora);

        List<LocalDateTime> findFechasOcupadasPorMedico(
                        MedicoId medicoId,
                        LocalDateTime desde,
                        LocalDateTime hasta);

        List<Cita> findByMedicoId(MedicoId medicoId);

        List<Cita> findByFecha(LocalDateTime inicio, LocalDateTime fin);

        List<Cita> findAll();

        List<Cita> findByMedicoIdAndFecha(MedicoId medicoId,
                        LocalDateTime inicio,
                        LocalDateTime fin);

        List<Cita> findByPacienteId(PacienteId pacienteId);

        List<Cita> findByPacienteIdAndFecha(PacienteId pacienteId,
                        LocalDateTime inicio,
                        LocalDateTime fin);
}