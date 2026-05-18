package com.almacen.empleados_service.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpleadoResponse {

    private Long id;
    private String nombre;
    private String rut;
    private String cargo;
    private String turno;
    private String telefono;
    private String email;
    private LocalDate fechaInicioContrato;
    private Boolean activo;
    private Long usuarioId;

    private List<VentaResponse> ventas;
}