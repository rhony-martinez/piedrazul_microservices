package com.piedrazul.usuarios.domain.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    private Integer id;
    private String username;
    private String passwordHash;
    private EstadoUsuario estado;
    private Integer personaId;
    private int intentosFallidos;

    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3) {
            this.estado = EstadoUsuario.INACTIVO;
        }
    }

    public void resetearIntentosFallidos() {
        this.intentosFallidos = 0;
    }

    public boolean estaActivo() {
        return this.estado == EstadoUsuario.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoUsuario.INACTIVO;
    }

    public void asignarRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.roles.add(rol);
    }

    public boolean tieneRol(String nombreRol) {
        return this.roles.stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase(nombreRol));
    }

    public void cambiarPasswordHash(String nuevoPasswordHash) {
        if (nuevoPasswordHash == null || nuevoPasswordHash.isBlank()) {
            throw new IllegalArgumentException("El password hash no puede ser vacío");
        }
        this.passwordHash = nuevoPasswordHash;
    }
}
