package com.almacen.proveedores_service.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class ProveedorResponse {

    private Long id;
    private String nombre;
    private String contacto;
    private String rut;
}
