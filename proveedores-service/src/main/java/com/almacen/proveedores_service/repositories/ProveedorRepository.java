package com.almacen.proveedores_service.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.proveedores_service.models.ProveedorModel;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorModel,Long>
{

}
