package com.piedrazul.personas.infrastructure.persistence.springdata;

import com.piedrazul.personas.infrastructure.persistence.entity.EspecialidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataEspecialidadRepository extends JpaRepository<EspecialidadEntity, Long> {
    Optional<EspecialidadEntity> findByNombre(String nombre);
}