package com.almacen.inventario_service.controller;


import com.almacen.inventario_service.dtos.request.InventarioRequest;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.services.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponse> obtenerPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<InventarioResponse>> obtenerBajoStock() {
        return ResponseEntity.ok(inventarioService.obtenerBajoStock());
    }

    @PostMapping
    public ResponseEntity<InventarioResponse> crear(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse nuevo = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado");
    }
}
