package com.almacen.pedidos_service.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoItem {

    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El id del producto es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad del producto", example = "10")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}
