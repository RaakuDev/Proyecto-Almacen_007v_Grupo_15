package com.almacen.productos_service.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
@Schema(description = "Entidad que representa un producto")
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Código de un producto", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre de un producto", example = "Leche Colun 1lt")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Precio de un producto", example = "1300")
    private Long precio;

    @Schema(description = "ID de una categoría", example = "1")
    private Long categoriaId;

    @Schema(description = "ID de un proveedor", example = "1")
    private Long proveedorId;
}