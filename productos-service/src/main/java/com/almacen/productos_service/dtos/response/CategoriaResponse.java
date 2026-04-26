package com.almacen.productos_service.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
}