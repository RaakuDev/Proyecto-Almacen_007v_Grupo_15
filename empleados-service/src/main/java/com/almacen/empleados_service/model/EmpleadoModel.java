package com.almacen.empleados_service.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private String turno;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate fechaInicioContrato;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = true)
    private Long usuarioId;
}