package com.almacen.pedidos_service.dtos.request;

import java.time.LocalDate;
import java.util.List;

import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.dtos.response.ProductoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidosRequest {

    @NotNull(message = "La fecha del pedido es obligatoria")
    private LocalDate fechaPedido;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotNull(message = "El id del proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "El id del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "Los productos son obligatorios")
    private List<PedidoItem> items;
}