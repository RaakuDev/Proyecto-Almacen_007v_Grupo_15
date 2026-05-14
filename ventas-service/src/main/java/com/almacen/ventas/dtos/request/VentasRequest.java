package com.almacen.ventas.dtos.request;

import java.math.BigDecimal;

import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VentasRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteID;

    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoDePago metodoPago;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private TipoComprobante tipoComprobante;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal no puede ser negativo")
    private BigDecimal subTotal;

    @NotNull(message = "El descuento total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento no puede ser negativo")
    private BigDecimal descuentoTotal;

    @NotNull(message = "El impuesto total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El impuesto no puede ser negativo")
    private BigDecimal impuestoTotal;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total debe ser mayor o igual a cero")
    private BigDecimal total;

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;

    @NotNull(message = "El vuelto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El vuelto no puede ser negativo")
    private BigDecimal vuelto;

    @NotNull(message = "El número de comprobante es obligatorio")
    @Size(max = 50, message = "El número de comprobante no puede exceder 50 caracteres")
    private String numeroComprobante;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}