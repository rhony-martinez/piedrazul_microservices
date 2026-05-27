package com.piedrazul.citas.domain.model;

import com.piedrazul.citas.domain.exception.CitaNoCancelableException;
import com.piedrazul.citas.domain.exception.CitaNoMarcableException;
import com.piedrazul.citas.domain.exception.CitaNoReagendableException;
import com.piedrazul.citas.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Cita")
class CitaTest {

    private CitaId citaId;
    private PacienteId pacienteId;
    private MedicoId medicoId;
    private UsuarioId creadoPor;
    private LocalDateTime fechaHora;
    private Cita cita;

    @BeforeEach
    void setUp() {
        citaId = CitaId.generate();
        pacienteId = PacienteId.of(1L);
        medicoId = MedicoId.of(1L);
        creadoPor = UsuarioId.of(1L);
        fechaHora = LocalDateTime.now().plusDays(2);

        cita = new Cita(citaId, pacienteId, medicoId, EspecialidadMedica.GENERAL, creadoPor, fechaHora);
    }

    @Test
    @DisplayName("Debería crear una cita correctamente")
    void testCrearCita() {
        assertNotNull(cita);
        assertEquals(citaId, cita.getId());
        assertEquals(pacienteId, cita.getPacienteId());
        assertEquals(medicoId, cita.getMedicoId());
        assertEquals(EspecialidadMedica.GENERAL, cita.getEspecialidad());
        assertEquals(creadoPor, cita.getCreadoPor());
        assertEquals(fechaHora, cita.getFechaHora());
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());
        assertNotNull(cita.getAudit());
    }

    @Test
    @DisplayName("Debería cancelar la cita correctamente")
    void testCancelar() {
        cita.cancelar("Paciente no pudo asistir");

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        assertEquals("Paciente no pudo asistir", cita.getMotivoCancelacion());
    }

    @Test
    @DisplayName("No debería cancelar una cita ya cancelada")
    void testCancelarCitaYaCancelada() {
        cita.cancelar("Motivo");

        assertThrows(CitaNoCancelableException.class, () -> {
            cita.cancelar("Otro motivo");
        });
    }

    @Test
    @DisplayName("Debería reagendar la cita correctamente")
    void testReagendar() {
        cita = reconstruirCitaConEstado(EstadoCita.CONFIRMADA);

        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);
        // Asegurar que nuevaFecha tenga disponibilidad (lunes a viernes)
        while (nuevaFecha.getDayOfWeek() == DayOfWeek.SATURDAY || nuevaFecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nuevaFecha = nuevaFecha.plusDays(1);
        }
        nuevaFecha = nuevaFecha.withHour(10).withMinute(0).withSecond(0).withNano(0);

        DisponibilidadSnapshot disponibilidad = crearDisponibilidadSnapshot(medicoId, nuevaFecha);

        cita.reagendar(nuevaFecha, disponibilidad);

        assertEquals(EstadoCita.REAGENDADA, cita.getEstado());
        assertEquals(nuevaFecha, cita.getFechaHora());
    }

    @Test
    @DisplayName("No debería reagendar una cita cancelada")
    void testReagendarCitaCancelada() {
        cita.cancelar("Motivo");
        DisponibilidadSnapshot disponibilidad = crearDisponibilidadSnapshot(medicoId, fechaHora);
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);

        assertThrows(CitaNoReagendableException.class, () -> {
            cita.reagendar(nuevaFecha, disponibilidad);
        });
    }

    @Test
    @DisplayName("Debería marcar como atendida")
    void testMarcarComoAtendida() {
        cita = reconstruirCitaConEstado(EstadoCita.CONFIRMADA);

        cita.marcarComoAtendida();

        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
        assertNotNull(cita.getFechaAsistencia());
    }

    @Test
    @DisplayName("No debería marcar como atendida una cita no confirmada")
    void testMarcarComoAtendidaNoConfirmada() {
        assertThrows(CitaNoMarcableException.class, () -> {
            cita.marcarComoAtendida();
        });
    }

    @Test
    @DisplayName("Debería marcar como no asistida (con fecha pasada)")
    void testMarcarComoNoAsistida() {
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(2);
        cita = reconstruirCitaConEstado(EstadoCita.CONFIRMADA, fechaPasada);

        cita.marcarComoNoAsistida();

        assertEquals(EstadoCita.NO_ASISTIDA, cita.getEstado());
        assertNotNull(cita.getFechaAsistencia());
    }

    @Test
    @DisplayName("Debería reconstruir correctamente desde base de datos")
    void testReconstruir() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Cita citaReconstruida = Cita.reconstruir(
                citaId, pacienteId, medicoId, EspecialidadMedica.GENERAL, creadoPor, fechaHora,
                EstadoCita.PROGRAMADA, null, null,
                createdAt, updatedAt, "system"
        );

        assertNotNull(citaReconstruida);
        assertEquals(createdAt, citaReconstruida.getAudit().getCreatedAt());
        assertEquals(updatedAt, citaReconstruida.getAudit().getUpdatedAt());
    }

    // Métodos auxiliares
    private Cita reconstruirCitaConEstado(EstadoCita estado) {
        return Cita.reconstruir(
                citaId, pacienteId, medicoId, EspecialidadMedica.GENERAL, creadoPor, fechaHora,
                estado, null, null,
                LocalDateTime.now(), LocalDateTime.now(), "system"
        );
    }

    private Cita reconstruirCitaConEstado(EstadoCita estado, LocalDateTime fecha) {
        return Cita.reconstruir(
                citaId, pacienteId, medicoId, EspecialidadMedica.GENERAL, creadoPor, fecha,
                estado, null, null,
                LocalDateTime.now(), LocalDateTime.now(), "system"
        );
    }

    private DisponibilidadSnapshot crearDisponibilidadSnapshot(MedicoId medicoId, LocalDateTime fechaHora) {
        DisponibilidadSnapshot snapshot = new DisponibilidadSnapshot(medicoId, 30);
        DayOfWeek dia = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        // Crear un rango que incluya la hora exacta
        LocalTime start = hora.minusMinutes(hora.getMinute());
        LocalTime end = start.plusHours(2);

        snapshot.agregarHorarioSemanal(dia, new TimeRange(start, end));
        return snapshot;
    }
}