package com.almacen.DetalleVentas.dtos.response;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long id;
    private String nombre;
    private Long precio;
    private int stock;
    private Long categoriaId;
}