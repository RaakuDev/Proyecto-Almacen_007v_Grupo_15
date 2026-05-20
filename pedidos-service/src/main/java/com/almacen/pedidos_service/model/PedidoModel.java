package com.almacen.pedidos_service.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class PedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaPedido;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Long proveedorId;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String productosIds;
}