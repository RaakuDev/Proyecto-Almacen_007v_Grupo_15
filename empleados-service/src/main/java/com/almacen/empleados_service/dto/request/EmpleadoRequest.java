package com.almacen.empleados_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotBlank(message = "El turno es obligatorio")
    private String turno;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotNull(message = "La fecha de inicio de contrato es obligatoria")
    private LocalDate fechaInicioContrato;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    private Long usuarioId;
}