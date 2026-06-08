package com.almacen.inventario_service.models;


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
@Table(name = "inventario")
@Schema(description = "Entidad que muestra el stock de un producto")
public class InventarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Código del inventario", example = "2")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Stock actual de un producto", example = "20")
    private int stockActual;

    @Column(nullable = false)
    @Schema(description = "Cuando un producto esta con bajo stock", example = "5")
    private int stockMinimo;

    @Column(nullable = false)
    @Schema(description = "ID del producto", example = "1")
    private Long productoId;
}
