package com.almacen.ventas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.ventas.models.VentasModel;

@Repository
public interface VentasRepository extends JpaRepository<VentasModel, Long>{

}
