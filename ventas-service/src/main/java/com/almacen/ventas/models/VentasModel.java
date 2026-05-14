package com.almacen.ventas.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ventas")
public class VentasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVenta;

    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuentoTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestoTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetodoDePago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal vuelto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private EstadoVentas estadoVenta;

    @Column(nullable = false)
    private Long clienteID;

    @Column(nullable = false)
    private Long empleadoId;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroComprobante;

    @Column(length = 500)
    private String observaciones;



}
