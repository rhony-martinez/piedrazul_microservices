package com.piedrazul.citas.domain.model;

import com.piedrazul.citas.domain.valueobjects.MedicoId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de MedicoSnapshot")
class MedicoSnapshotTest {

    @Test
    @DisplayName("Debería crear un snapshot de médico correctamente")
    void testCrear() {
        MedicoId medicoId = MedicoId.of(1L);
        MedicoSnapshot snapshot = new MedicoSnapshot(
                medicoId,
                "Dr. Juan Pérez",
                "juan@email.com",
                "Cardiología",
                EstadoMedico.ACTIVO
        );

        assertNotNull(snapshot);
        assertEquals(medicoId, snapshot.getId());
        assertEquals("Dr. Juan Pérez", snapshot.getNombreCompleto());
        assertEquals("juan@email.com", snapshot.getEmail());
        assertEquals("Cardiología", snapshot.getEspecialidad());
        assertEquals(EstadoMedico.ACTIVO, snapshot.getEstado());
        assertNotNull(snapshot.getActualizadoEn());
    }

    @Test
    @DisplayName("estaActivo() debería devolver true si el médico está ACTIVO")
    void testEstaActivo() {
        MedicoId medicoId = MedicoId.of(1L);
        MedicoSnapshot activo = new MedicoSnapshot(medicoId, "Dr. Activo", "a@a.com", "Cardiología", EstadoMedico.ACTIVO);
        MedicoSnapshot inactivo = new MedicoSnapshot(medicoId, "Dr. Inactivo", "i@i.com", "Cardiología", EstadoMedico.INACTIVO);

        assertTrue(activo.estaActivo());
        assertFalse(inactivo.estaActivo());
    }
}