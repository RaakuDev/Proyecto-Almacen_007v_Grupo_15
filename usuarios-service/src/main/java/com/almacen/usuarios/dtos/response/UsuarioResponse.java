package com.almacen.usuarios.dtos.response;

import com.almacen.usuarios.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Respuesta con los datos de un usuario")
@Data
@Builder
public class UsuarioResponse {

    @Schema(description = "Identificador del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;
    
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "RUT del usuario", example = "12345678-9")
    private String rut;

    @Schema(description = "Correo electrónico", example = "juan.perez@ejemplo.com")
    private String email;

    @Schema(description = "Rol del usuario")
    private Rol rol;

    @Schema(description = "Estado activo del usuario")
    private boolean estado;

    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @Schema(description = "Dirección del usuario", example = "Av. Siempre Viva 123")
    private String direccion;

}

// Cabres... No ingresamos password aqui por que es dato sensible y no queremos que Salga.