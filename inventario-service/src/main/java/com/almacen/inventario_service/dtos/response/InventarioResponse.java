package com.almacen.inventario_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventarioResponse {

    private Long id;
    private Integer stockActual;
    private Integer stockMinimo;
    private Long productoId;

    private ProductoResponse producto;
}