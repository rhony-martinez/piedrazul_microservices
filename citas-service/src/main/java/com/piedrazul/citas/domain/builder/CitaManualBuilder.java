package com.piedrazul.citas.domain.builder;

import com.piedrazul.citas.domain.model.Cita;

public class CitaManualBuilder extends CitaBuilder {

    @Override
    public Cita build() {

        validarPacienteActivo();
        validarMedicoActivo();

        // podría permitir excepciones
        validarDisponibilidad();

        return construir();
    }
}
