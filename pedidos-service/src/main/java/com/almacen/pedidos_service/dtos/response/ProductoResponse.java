package com.almacen.pedidos_service.dtos.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductoResponse {
    private Long id;
    private String nombre;
    private Long precio;
    private Integer stock;
    private Long categoriaId;
}
