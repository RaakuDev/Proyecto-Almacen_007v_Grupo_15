package com.almacen.productos_service.dtos.response;

import lombok.Data;

@Data
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
}