package com.piedrazul.usuarios.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioRolId implements Serializable {

    @Column(name = "usu_id")
    private Integer usuarioId;

    @Column(name = "rol_id")
    private Integer rolId;
}
