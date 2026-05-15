package com.almacen.usuarios.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    private String password;
}