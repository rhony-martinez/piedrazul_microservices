package com.piedrazul.personas.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DisponibilidadResponse {

    private Long medicoId;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
}