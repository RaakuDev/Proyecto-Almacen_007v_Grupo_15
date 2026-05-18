package com.almacen.empleados_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VentaResponse {

    private Long idVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private Long clienteID;
}