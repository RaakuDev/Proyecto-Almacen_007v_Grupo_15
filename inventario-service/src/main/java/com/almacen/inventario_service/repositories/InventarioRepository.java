package com.almacen.inventario_service.repositories;

import com.almacen.inventario_service.models.InventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioModel, Long> {

    Optional<InventarioModel> findByProductoId(Long productoId);
}
