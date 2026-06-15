package com.almacen.proveedores_service.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProveedorRequest {

    @Schema(description = "Nombre del proveedor", example = "Distribuidora Central")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Contacto del proveedor", example = "Juan Pérez")
    @NotBlank(message = "El contacto es obligatorio")
    private String contacto;

    @Schema(description = "RUT del proveedor", example = "76543210-9")
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;
}