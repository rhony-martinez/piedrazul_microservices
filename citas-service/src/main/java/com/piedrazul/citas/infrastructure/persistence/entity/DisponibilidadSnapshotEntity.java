package com.piedrazul.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "disponibilidad_snapshot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadSnapshotEntity {

    @Id
    private Long medicoId;

    @Column(columnDefinition = "jsonb")
    private Map<String, Object> horariosSemanales;

    @Column(columnDefinition = "jsonb")
    private Set<LocalDateTime> bloqueosEspecificos;

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