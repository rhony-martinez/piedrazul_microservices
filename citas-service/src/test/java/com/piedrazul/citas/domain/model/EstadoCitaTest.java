package com.piedrazul.citas.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de EstadoCita")
class EstadoCitaTest {

    @Test
    @DisplayName("Debe contener todos los estados esperados")
    void testCantidadEstados() {
        EstadoCita[] estados = EstadoCita.values();
        assertEquals(6, estados.length);
    }

    @Test
    @DisplayName("Cada estado debe tener su descripción")
    void testDescripciones() {
        assertEquals("Programada", EstadoCita.PROGRAMADA.getDescripcion());
        assertEquals("Confirmada", EstadoCita.CONFIRMADA.getDescripcion());
        assertEquals("Atendida", EstadoCita.ATENDIDA.getDescripcion());
        assertEquals("Cancelada", EstadoCita.CANCELADA.getDescripcion());
        assertEquals("No Asistida", EstadoCita.NO_ASISTIDA.getDescripcion());
        assertEquals("Reagendada", EstadoCita.REAGENDADA.getDescripcion());
    }

    @Test
    @DisplayName("Debe permitir cancelarse en estados PROGRAMADA, CONFIRMADA y REAGENDADA")
    void testPuedeCancelarse() {
        assertTrue(EstadoCita.PROGRAMADA.puedeCancelarse());
        assertTrue(EstadoCita.CONFIRMADA.puedeCancelarse());
        assertTrue(EstadoCita.REAGENDADA.puedeCancelarse());

        assertFalse(EstadoCita.ATENDIDA.puedeCancelarse());
        assertFalse(EstadoCita.CANCELADA.puedeCancelarse());
        assertFalse(EstadoCita.NO_ASISTIDA.puedeCancelarse());
    }

    @Test
    @DisplayName("Debe permitir reagendarse en estados PROGRAMADA y CONFIRMADA")
    void testPuedeReagendarse() {
        assertTrue(EstadoCita.PROGRAMADA.puedeReagendarse());
        assertTrue(EstadoCita.CONFIRMADA.puedeReagendarse());

        assertFalse(EstadoCita.ATENDIDA.puedeReagendarse());
        assertFalse(EstadoCita.CANCELADA.puedeReagendarse());
        assertFalse(EstadoCita.NO_ASISTIDA.puedeReagendarse());
        assertFalse(EstadoCita.REAGENDADA.puedeReagendarse());
    }
}