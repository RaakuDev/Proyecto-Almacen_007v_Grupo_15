package com.almacen.productos_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoResponse {

    private Long id;
    private String nombre;
    private Long precio;

    private Long categoriaId;
    private Long proveedorId;

    private CategoriaResponse categoria;
    private ProveedorResponse proveedor;
}