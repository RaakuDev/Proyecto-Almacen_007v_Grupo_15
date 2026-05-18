package com.almacen.productos_service.repositories;

import com.almacen.productos_service.models.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

    List<ProductoModel> findByCategoriaId(Long categoriaId);

    List<ProductoModel> findByProveedorId(Long proveedorId);

}