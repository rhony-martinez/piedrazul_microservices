// infrastructure/persistence/repository/SpringDataCitaRepository.java
package com.piedrazul.citas.infrastructure.persistence.repository;

import com.piedrazul.citas.infrastructure.persistence.entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringDataCitaRepository extends JpaRepository<CitaEntity, String> {
    Optional<CitaEntity> findById(String id);
    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);
}