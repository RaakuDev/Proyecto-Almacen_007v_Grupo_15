package com.almacen.pedidos_service.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PedidosRequest {

    @Schema(description = "Fecha del pedido", example = "2024-01-15")
    @NotNull(message = "La fecha del pedido es obligatoria")
    private LocalDate fechaPedido;

    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @Schema(description = "ID del proveedor", example = "1")
    @NotNull(message = "El id del proveedor es obligatorio")
    private Long proveedorId;

    @Schema(description = "ID del cliente", example = "1")
    @NotNull(message = "El id del cliente es obligatorio")
    private Long clienteId;

    @Schema(description = "Lista de items del pedido")
    @NotNull(message = "Los productos son obligatorios")
    private List<PedidoItem> items;
}