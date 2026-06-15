package com.almacen.categorias_service.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @Schema(description = "Nombre de la categoría", example = "Lácteos")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Descripción de la categoría", example = "Leche, queso, yogur")
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}