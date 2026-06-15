package com.almacen.usuarios.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Datos de inicio de sesión")
@Data
public class LoginRequest {

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "Passw0rd!23")
    private String password;
}