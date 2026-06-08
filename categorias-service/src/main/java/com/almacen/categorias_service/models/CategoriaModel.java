package com.almacen.categorias_service.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categorias")
@Schema(description = "Entidad que representa una categoría de un producto")
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Código de la categoría", example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre de la categoría", example = "Lácteos")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    @Schema(description = "Descripción de la categoría", example = "Leche, queso, yogurt")
    private String descripcion;
}

