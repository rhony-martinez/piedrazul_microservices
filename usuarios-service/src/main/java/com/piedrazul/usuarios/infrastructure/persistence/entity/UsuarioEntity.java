package com.piedrazul.usuarios.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "usu_password", nullable = false, length = 255)
    private String password;

    @Column(name = "usu_estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "per_id", nullable = false, unique = true)
    private Integer personaId;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioRolEntity> roles = new HashSet<>();
}