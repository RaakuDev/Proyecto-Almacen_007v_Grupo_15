package com.almacen.DetalleVentas.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.DetalleVentas.dtos.request.DetalleRequest;
import com.almacen.DetalleVentas.dtos.response.DetalleResponse;
import com.almacen.DetalleVentas.services.DetalleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/detalles")
@RequiredArgsConstructor
public class DetalleController {

    private final DetalleService detalleService;

    @GetMapping
    public ResponseEntity<List<DetalleResponse>> obtenerTodos() {
        return ResponseEntity.ok(detalleService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.obtenerPorId(id));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DetalleResponse>> obtenerPorVentaId(@PathVariable Long ventaId) {
        return ResponseEntity.ok(detalleService.obtenerPorVentaId(ventaId));
    }

    @PostMapping
    public ResponseEntity<DetalleResponse> guardar(@Valid @RequestBody DetalleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detalleService.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DetalleRequest request
    ) {
        return ResponseEntity.ok(detalleService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}