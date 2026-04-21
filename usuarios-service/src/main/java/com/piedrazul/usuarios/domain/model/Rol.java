package com.piedrazul.usuarios.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Rol {
    private Integer id;
    private String nombre;
}
