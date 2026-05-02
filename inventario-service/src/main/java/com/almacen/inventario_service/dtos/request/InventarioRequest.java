package com.almacen.inventario_service.dtos.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioRequest {

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser menos que 0")
    private Integer stockActual;

    @NotNull(message = "El stock minimo es obligatorio")
    @Min(value = 0, message = "El stock minimo no puede ser menor a 0")
    private Integer stockMinimo;

    @NotNull(message = "id producto no puede estar vacío")
    private Long productoId;
}
