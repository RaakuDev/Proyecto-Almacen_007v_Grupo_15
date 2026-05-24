package com.almacen.pedidos_service.dtos.response;

import java.time.LocalDate;
import java.util.List;

import com.almacen.pedidos_service.dtos.request.PedidoItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidosResponse {

    private Long id;
    private LocalDate fechaPedido;
    private String estado;

    private Long proveedorId;
    private ProveedorResponse proveedor;
    private Long clienteId;
    private ClienteResponse cliente;

    private List<ProductoResponse> productos;
}