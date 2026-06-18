package com.almacen.clientes.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ClientesResponse {

    private long id;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
    

}
