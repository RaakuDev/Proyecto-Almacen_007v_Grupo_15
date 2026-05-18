package com.almacen.proveedores_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El contacto es obligatorio")
    private String contacto;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;
}