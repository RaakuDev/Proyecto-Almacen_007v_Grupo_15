package com.almacen.ventas.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VentasResponse {

    private Long idVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal subTotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestoTotal;
    private BigDecimal total;
    private MetodoDePago metodoPago;
    private TipoComprobante tipoComprobante;
    private BigDecimal montoPagado;
    private BigDecimal vuelto;
    private EstadoVentas estadoVenta;
    private Long clienteID;
    private Long empleadoId;
    private String numeroComprobante;
    private String observaciones;
}