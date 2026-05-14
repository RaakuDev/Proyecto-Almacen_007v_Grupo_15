package com.almacen.DetalleVentas.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleRequest {

    @NotNull
    private Long ventaId;

    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioUnitario;
}
