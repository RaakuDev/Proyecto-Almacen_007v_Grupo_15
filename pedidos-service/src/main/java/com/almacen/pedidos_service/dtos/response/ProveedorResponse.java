package com.almacen.pedidos_service.dtos.response;

import lombok.Data;

@Data
public class ProveedorResponse {
    
    private Long id;
    private String nombre;
    private String contacto;
    private String rut;
    
}
