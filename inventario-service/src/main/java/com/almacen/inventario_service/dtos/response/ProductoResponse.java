package com.almacen.inventario_service.dtos.response;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long id;
    private String nombre;
    private Long precio;
    private Long categoriaId;
}
