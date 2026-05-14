package com.almacen.ventas.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.ventas.Services.VentasServices;
import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.VentasResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentasController {

    private final VentasServices ventasServices;

    public VentasController(VentasServices ventasServices) {
        this.ventasServices = ventasServices;
    }

    @GetMapping
    public ResponseEntity<List<VentasResponse>> obtenerTodas() {
        return ResponseEntity.ok(ventasServices.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentasResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventasServices.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<VentasResponse> guardar(@Valid @RequestBody VentasRequest request) {
        VentasResponse nuevaVenta = ventasServices.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentasResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VentasRequest request) {
        return ResponseEntity.ok(ventasServices.actualizar(id, request));
    }

    @PutMapping("/{id}/recalcular-total")
    public ResponseEntity<VentasResponse> recalcularTotal(@PathVariable Long id) {
        return ResponseEntity.ok(ventasServices.recalcularTotal(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        ventasServices.eliminar(id);
        return ResponseEntity.ok("Venta eliminada correctamente");
    }
}