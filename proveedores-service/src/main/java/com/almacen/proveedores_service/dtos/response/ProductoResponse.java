package com.almacen.proveedores_service.dtos.response;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private Long categoriaId;
    private Long proveedorId;

}