package com.almacen.ventas.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleVentaRequest {

    private Long ventaId;
    private Long productoId;
    private Integer cantidad;
}