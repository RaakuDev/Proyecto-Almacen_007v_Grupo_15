package com.almacen.DetalleVentas.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleRequest {

    @Schema(description = "ID de la venta", example = "1")
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad del producto", example = "5")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}