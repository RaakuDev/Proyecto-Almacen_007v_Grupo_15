package com.almacen.productos_service.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoRequest {

    @Schema(description = "Nombre del producto", example = "Arroz 1kg")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Precio del producto en pesos chilenos", example = "1500")
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0)
    private Long precio;

    @Schema(description = "ID de la categoría del producto", example = "1")
    @NotNull(message = "El id de categoría es obligatorio")
    private Long categoriaId;

    @Schema(description = "ID del proveedor del producto", example = "1")
    @NotNull(message = "El id de proveedor es obligatorio")
    private Long proveedorId;

    @Schema(description = "Stock inicial del producto", example = "20")
    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer stockInicial;

    @Schema(description = "Stock mínimo de un producto (bajo stock)", example = "5")
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
}