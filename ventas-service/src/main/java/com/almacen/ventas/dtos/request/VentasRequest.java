package com.almacen.ventas.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VentasRequest {
    @Schema(description = "Id del cliente al que pertenece la venta", example = "Juan Pérez")
    private Long clienteId;

    @Schema(description = "Id del empleado que hizo la venta", example = "Pedro González")
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;

    @Schema(description = "Nombre del método con el cual se paga", example = "EFECTIVO")
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoDePago metodoPago;

    @Schema(description = "Tipo de comprobante que devuelve la venta", example = "BOLETA")
    @NotNull(message = "El tipo de comprobante es obligatorio")
    private TipoComprobante tipoComprobante;

    @Schema(description = "El monto con el que el cliente paga", example = "6000.00")
    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;

    @Schema(description = "Número asignado al comprobante de venta", example = "B-0001")
    @NotNull(message = "El número de comprobante es obligatorio")
    @Size(max = 50, message = "El número de comprobante no puede exceder 50 caracteres")
    private String numeroComprobante;

    @Schema(description = "Observaciones hechas en la venta", example = "Venta normal")
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