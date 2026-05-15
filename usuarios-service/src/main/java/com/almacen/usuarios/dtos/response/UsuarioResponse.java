package com.almacen.usuarios.dtos.response;

import com.almacen.usuarios.enums.Rol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UsuarioResponse {

    private Long id;

    private String username;
    
    private String nombre;

    private String rut;

    private String email;

    private Rol rol;

    private boolean estado;

    private String telefono;

    private String direccion;



}

// Cabres... No ingresamos password aqui por que es dato sensible y no queremos que Salga.