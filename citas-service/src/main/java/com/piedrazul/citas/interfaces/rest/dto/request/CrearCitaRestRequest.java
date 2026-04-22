package com.piedrazul.citas.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCitaRestRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    @Positive(message = "El ID del médico debe ser positivo")
    private Long medicoId;

    @NotNull(message = "El ID del usuario creador es obligatorio")
    @Positive(message = "El ID del usuario creador debe ser positivo")
    private Long usuarioCreadorId;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;
}