package com.almacen.pedidos_service.dtos.response;

import lombok.Data;

@Data
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
}
