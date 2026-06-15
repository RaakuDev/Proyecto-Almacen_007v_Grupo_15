package com.almacen.categorias_service.repositories;

import com.almacen.categorias_service.models.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
}
