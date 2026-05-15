package com.almacen.usuarios.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;



    // PARA CONSULTAR AL PROFE SI LO PODEMOS OCUPAR
    // Guarda detalles extras de errores de validación
    // Ejemplo:
    // "email" : "El email es obligatorio"
    // Asi el Postman nos responde el error mucho mas pro
    private Map<String, String> details;
}