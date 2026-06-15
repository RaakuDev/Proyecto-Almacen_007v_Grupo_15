package com.almacen.usuarios.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Respuesta que contiene el token JWT generado al iniciar sesión")
@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJI...")
    private String token;

    @Schema(description = "Tipo de esquema de autenticación", example = "Bearer")
    private String tipo;
}