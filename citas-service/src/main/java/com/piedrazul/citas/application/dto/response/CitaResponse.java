package com.piedrazul.citas.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaResponse {
    private String id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private LocalDateTime fechaHora;
    private String estado;
    private String motivoCancelacion;
    private LocalDateTime fechaAsistencia;
    private LocalDateTime createdAt;
}