package com.almacen.ventas.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ventas")
public class VentasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(name = "sub_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "descuento_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuentoTotal;

    @Column(name = "impuesto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestoTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoDePago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal vuelto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta", nullable = false, length = 20)
    private EstadoVentas estadoVenta;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(name = "numero_comprobante", nullable = false, unique = true, length = 50)
    private String numeroComprobante;

    @Column(length = 500)
    private String observaciones;
}