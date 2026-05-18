package com.almacen.clientes.dtos.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PedidoResponse {

    private Long id;
    private LocalDate fecha;
    private String estado;
    private Long total;
    private Long clienteId;
}