package com.almacen.DetalleVentas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.DetalleVentas.models.DetalleModel;

@Repository
public interface DetalleRepository extends JpaRepository<DetalleModel, Long> {

    List<DetalleModel> findByVentaId(Long ventaId);
}