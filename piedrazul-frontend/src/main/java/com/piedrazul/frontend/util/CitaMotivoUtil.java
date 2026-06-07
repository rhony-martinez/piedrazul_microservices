package com.piedrazul.frontend.util;

import com.piedrazul.frontend.dto.response.CitaResponse;

public final class CitaMotivoUtil {

    private CitaMotivoUtil() {
    }

    public static String resolverMotivo(CitaResponse cita) {
        if (cita == null) {
            return "-";
        }
        if (cita.getMotivoAgendamiento() != null && !cita.getMotivoAgendamiento().isBlank()) {
            return cita.getMotivoAgendamiento().trim();
        }
        if (cita.getMotivoCancelacion() != null && !cita.getMotivoCancelacion().isBlank()) {
            return cita.getMotivoCancelacion().trim();
        }
        return "-";
    }
}
