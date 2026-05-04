package com.piedrazul.citas.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configuracion_sistema")
@Getter
@Setter
public class ConfiguracionSistemaEntity {

    @Id
    private Long id = 1L; // singleton

    private Integer semanasDisponibles;
}