package com.almacen.inventario_service.controller;

import com.almacen.inventario_service.dtos.request.InventarioRequest;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.services.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Operaciones relacionadas con los inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los inventarios", description = "Obtiene una lista de todo el stock de los productos")
    public ResponseEntity<List<InventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID", description = "Obtiene el stock por ID ingresado")
    public ResponseEntity<InventarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventario por ID de producto", description = "Obtiene el stock del ID producto ingresado")
    public ResponseEntity<InventarioResponse> obtenerPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener inventario por ID de categoría", description = "Obtiene el stock de todos los productos de una categoría")
    public ResponseEntity<List<InventarioResponse>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(inventarioService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/bajo-stock")
    @Operation(summary = "Obtener inventario de productos bajos en stock", description = "Muestra productos que estan con bajo stock")
    public ResponseEntity<List<InventarioResponse>> obtenerBajoStock() {
        return ResponseEntity.ok(inventarioService.obtenerBajoStock());
    }

    @PostMapping
    @Operation(summary = "Crear inventario para un producto", description = "Registra el stock de un producto por ID")
    public ResponseEntity<InventarioResponse> crear(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse nuevo = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario de un producto", description = "Actualiza el registro de stock de un producto por ID")
    public ResponseEntity<InventarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, request));
    }

    @PutMapping("/producto/{productoId}/aumentar/{cantidad}")
    @Operation(summary = "Aumentar el stock de un Producto", description = "Aumenta el stock del producto segun cantidad ingresada")
    public ResponseEntity<InventarioResponse> aumentarStock(
            @PathVariable Long productoId,
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(
                inventarioService.aumentarStock(productoId, cantidad));
    }

    @PutMapping("/producto/{productoId}/descontar/{cantidad}")
    @Operation(summary = "Descontar stock de un producto", description = "Resta stock a un producto segun ID")
    public ResponseEntity<InventarioResponse> descontarStock(
            @PathVariable Long productoId,
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(inventarioService.descontarStock(productoId, cantidad));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inventario de un producto", description = "Borra el inventario de un producto segun ID")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado");
    }
}