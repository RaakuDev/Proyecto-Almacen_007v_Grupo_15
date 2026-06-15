package com.almacen.categorias_service.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponse {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Lácteos")
    private String nombre;

    @Schema(description = "Descripción de la categoría", example = "Leche, queso, yogur")
    private String descripcion;
}
