package com.almacen.productos_service.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventarioRequest {

    private Long productoId;
    private Integer stockActual;
    private Integer stockMinimo;
}