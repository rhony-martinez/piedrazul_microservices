package com.piedrazul.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidad_snapshot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadSnapshotEntity {

    @Id
    private Long medicoId;

    // Usar TEXT en lugar de JSONB
    @Column(columnDefinition = "TEXT")
    private String horariosSemanales;

    @Column(columnDefinition = "TEXT")
    private String bloqueosEspecificos;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @Version
    private Long version;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}