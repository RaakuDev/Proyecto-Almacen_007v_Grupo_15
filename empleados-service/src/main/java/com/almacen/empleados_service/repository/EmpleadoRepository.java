package com.almacen.empleados_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.empleados_service.model.EmpleadoModel;

@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoModel, Long> {

    Optional<EmpleadoModel> findByUsuarioId(Long usuarioId);
}