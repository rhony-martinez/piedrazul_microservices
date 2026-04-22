package com.piedrazul.citas.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCitaRequest {
    private Long pacienteId;
    private Long medicoId;
    private Long usuarioCreadorId;
    private LocalDateTime fechaHora;
}