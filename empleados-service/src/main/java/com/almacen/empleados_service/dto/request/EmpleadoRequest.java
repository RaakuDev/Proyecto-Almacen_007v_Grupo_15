package com.almacen.empleados_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpleadoRequest {

    @Schema(description = "Nombre del empleado", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "RUT del empleado", example = "12345678-9")
    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @Schema(description = "Cargo del empleado", example = "Vendedor")
    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @Schema(description = "Turno del empleado", example = "Mañana")
    @NotBlank(message = "El turno es obligatorio")
    private String turno;

    @Schema(description = "Teléfono del empleado", example = "+56912345678")
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Schema(description = "Email del empleado", example = "juan.perez@almacen.cl")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Fecha de inicio de contrato", example = "2024-01-15")
    @NotNull(message = "La fecha de inicio de contrato es obligatoria")
    private LocalDate fechaInicioContrato;

    @Schema(description = "Estado activo del empleado", example = "true")
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @Schema(description = "ID del usuario asociado", example = "1")
    private Long usuarioId;
}