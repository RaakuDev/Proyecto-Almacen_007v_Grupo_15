package com.almacen.ventas.dtos.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetalleVentaResponse {

    private Long idDetalle;
    private Long ventaId;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;
}