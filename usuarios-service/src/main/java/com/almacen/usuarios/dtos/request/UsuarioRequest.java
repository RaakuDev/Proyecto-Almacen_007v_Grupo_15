package com.almacen.usuarios.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Datos necesarios para crear o actualizar un usuario")
@Data
public class UsuarioRequest {

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    @Schema(description = "Contraseña del usuario", example = "Passw0rd!23")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "El rut es obligatorio")
    @Schema(description = "RUT del usuario", example = "12345678-9")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Correo electrónico", example = "juan.perez@ejemplo.com")
    private String email;

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Rol asignado al usuario", example = "ADMIN")
    private String rol;

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Dirección del usuario", example = "Av. Siempre Viva 123")
    private String direccion;

}


// El rol lo ingreso como String para poder Recibir como texto y tolerar distintos formatos de entrada luego en el service  llamar a UPPER y normalizar para  transformar el texto a enum
// El estado no lo pusimos por que se controla desde el sistema y no directamente desde el usuario / cliente.