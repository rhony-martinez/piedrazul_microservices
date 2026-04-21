package com.piedrazul.usuarios.infrastructure.persistence.repository;

import com.piedrazul.usuarios.domain.model.Rol;
import com.piedrazul.usuarios.domain.repository.IRolRepository;
import com.piedrazul.usuarios.infrastructure.persistence.entity.RolEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class RolRepositoryImpl implements IRolRepository {

    private final SpringDataRolRepository rolRepository;

    public RolRepositoryImpl(SpringDataRolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .map(this::toDomain);
    }

    @Override
    public List<Rol> listarTodos() {
        return rolRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Rol toDomain(RolEntity entity) {
        return Rol.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }
}
