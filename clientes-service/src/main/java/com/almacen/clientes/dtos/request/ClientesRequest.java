package com.almacen.clientes.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ClientesRequest {

    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "RUT del cliente", example = "12.345.678-9")
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @Schema(description = "Dirección del cliente", example = "Av. Providencia 1234, Santiago")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Schema(description = "Teléfono del cliente", example = "+56912345678")
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Schema(description = "Email del cliente", example = "juan.perez@email.com")
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
