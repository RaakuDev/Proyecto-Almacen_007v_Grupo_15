package com.almacen.productos_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoResponse {

    private Long id;
    private String nombre;
    private Long precio;
    private int stock;
    private Long categoriaId;

    // Datos completos de la categoría desde categorias-service
    private CategoriaResponse categoria;
}