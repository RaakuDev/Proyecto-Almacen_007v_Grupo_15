package com.almacen.ventas.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VentasRequest {

    private Long clienteId;

    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoDePago metodoPago;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private TipoComprobante tipoComprobante;

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;

    @NotNull(message = "El número de comprobante es obligatorio")
    @Size(max = 50, message = "El número de comprobante no puede exceder 50 caracteres")
    private String numeroComprobante;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    @Valid
    @NotEmpty(message = "Los items de la venta son obligatorios")
    private List<ItemVentaRequest> items;

    private BigDecimal subTotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestoTotal;
    private BigDecimal total;
    private BigDecimal vuelto;
}