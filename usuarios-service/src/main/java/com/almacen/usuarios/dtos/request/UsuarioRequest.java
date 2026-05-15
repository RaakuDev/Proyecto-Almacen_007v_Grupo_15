package com.almacen.usuarios.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;


    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El rut es obligatorio")
    private String rut;


    @NotBlank(message = "El email es obligatorio")
    private String email;


    @NotBlank(message = "El username es obligatorio")
    private String rol;

    @NotBlank(message = "El username es obligatorio")
    private String telefono;


    @NotBlank(message = "El username es obligatorio")
    private String direccion;

}


// El rol lo ingreso como String para poder Recibir como texto y tolerar distintos formatos de entrada luego en el service  llamar a UPPER y normalizar para  transformar el texto a enum
// El estado no lo pusimos por que se controla desde el sistema y no directamente desde el usuario / cliente.