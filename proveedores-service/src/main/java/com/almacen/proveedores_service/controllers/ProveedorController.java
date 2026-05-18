package com.almacen.proveedores_service.controllers;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.proveedores_service.dtos.request.ProveedorRequest;
import com.almacen.proveedores_service.dtos.response.ProductoResponse;
import com.almacen.proveedores_service.dtos.response.ProveedorResponse;
import com.almacen.proveedores_service.services.ProveedorService;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> obtenerTodos() {
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @GetMapping("/{proveedorId}/productos")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosDelProveedor(
            @PathVariable Long proveedorId
    ) {
        return ResponseEntity.ok(
                proveedorService.obtenerProductosDelProveedor(proveedorId)
        );
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> guardar(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse nuevo = proveedorService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request
    ) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado");
    }
}