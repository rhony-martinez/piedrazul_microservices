// infrastructure/persistence/repository/SpringDataCitaRepository.java
package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.infrastructure.persistence.entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCitaRepository extends JpaRepository<CitaEntity, String> {
    Optional<CitaEntity> findById(String id);
    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);
    @Query("""
    SELECT c.fechaHora
    FROM CitaEntity c
    WHERE c.medicoId = :medicoId
    AND c.fechaHora BETWEEN :desde AND :hasta
    AND c.estado = 'PROGRAMADA'
    """)
    List<LocalDateTime> findFechasOcupadas(
            @Param("medicoId") Long medicoId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}