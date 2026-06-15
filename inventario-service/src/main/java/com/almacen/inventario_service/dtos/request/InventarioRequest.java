package com.almacen.inventario_service.dtos.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class InventarioRequest {

    @Schema(description = "Stock actual del producto", example = "50")
    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;

    @Schema(description = "Stock mínimo antes de solicitar reabastecimiento", example = "10")
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @Schema(description = "ID del producto al que pertenece el inventario", example = "1")
    @NotNull(message = "El id del producto es obligatorio")
    private Long productoId;
}
