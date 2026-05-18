package com.almacen.ventas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almacen.ventas.models.VentasModel;

public interface VentasRepository extends JpaRepository<VentasModel, Long> {

    Optional<VentasModel> findByNumeroComprobante(String numeroComprobante);

}